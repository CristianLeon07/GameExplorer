package com.example.consumoapijetpackcompose.data.repository

import android.content.Context
import android.util.Log
import com.example.consumoapijetpackcompose.R
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepositoryImpl,
    @ApplicationContext private val context: Context
) : AuthRepository {

    // ------------------------
    // CONFIGURACIÓN GOOGLE
    // ------------------------
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // ------------------------
    // LOGIN LOCAL
    // ------------------------

    /**
     * Login con email y contraseña (usuarios locales)
     * @return true si el login es exitoso
     */
    override suspend fun login(email: String, password: String): Boolean {
        val user = userRepository.getUserByEmail(email)
        return if (user != null && user.password == password) {
            userRepository.setUserLoggedIn(email)
            true
        } else {
            false
        }
    }

    // ------------------------
    // LOGIN GOOGLE
    // ------------------------

    /**
     * Login mediante Google con idToken
     * @return true si el login es exitoso
     */
    override suspend fun loginWithGoogle(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()

        val firebaseUser = firebaseAuth.currentUser ?: return false
        val userEmail = firebaseUser.email ?: return false
        val userName = firebaseUser.displayName ?: "Usuario"
        val userPhoto = firebaseUser.photoUrl?.toString()

        // Verifica si el usuario ya existe en Room
        val existingUser = userRepository.getUserByEmail(userEmail)
        if (existingUser == null) {
            // Registrar usuario Google y marcarlo como logueado
            userRepository.addUser(
                UserModel(
                    email = userEmail,
                    name = userName,
                    loginMethod = "google",
                    isLoggedIn = true,
                    photo = userPhoto
                )
            )
        } else {
            // Actualizar usuario existente y marcarlo como logueado
            userRepository.updateUser(
                existingUser.copy(
                    name = userName,
                    photo = userPhoto,
                    isLoggedIn = true
                )
            )
            userRepository.setUserLoggedIn(userEmail)
        }

        return true
    }

    // ------------------------
    // OBTENER USUARIO ACTUAL
    // ------------------------

    /**
     * Obtiene el usuario actualmente logueado (local o Google)
     */
    override suspend fun getCurrentUser(): UserModel? {
        val localUser = userRepository.getLoggedInUser()
        if (localUser != null) return localUser

        // Si no hay usuario local, intentar obtener de Firebase
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return UserModel(
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "Usuario",
            loginMethod = "google",
            isLoggedIn = true,
            photo = firebaseUser.photoUrl?.toString()
        )
    }

    // ------------------------
    // LOGOUT COMPLETO
    // ------------------------

    /**
     * Cierra la sesión del usuario actualmente logueado
     * Maneja tanto logout local como logout de Google/Firebase
     */
    override suspend fun logout() {
        // Logout de Google (si aplica)
        suspendCancellableCoroutine<Unit> { cont ->
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    cont.resume(Unit) {}
                }
            }
        }

        // Logout de Firebase
        firebaseAuth.signOut()

        // Limpiar sesión en Room
        val currentUser = userRepository.getLoggedInUser()
        currentUser?.let { user ->
            when (user.loginMethod) {
                "google" -> userRepository.deleteUser(user) // elimina usuario Google de Room
                "local" -> userRepository.clearSession()     // solo desloguea usuario local
            }
        }
    }
}
