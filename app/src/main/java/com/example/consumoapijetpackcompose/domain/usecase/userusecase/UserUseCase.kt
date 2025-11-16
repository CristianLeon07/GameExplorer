package com.example.consumoapijetpackcompose.domain.usecase.userusecase

import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.AuthRepository
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import javax.inject.Inject

/**Agregar un usuario
 * Este caso de uso encapsula la lógica de agregar un nuevo usuario a la base de datos (Room).
 * Solo se encarga de pasar el UserModel al repositorio.
 */
class AddUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: UserModel) {
        // Llama al repositorio para agregar el usuario
        userRepository.addUser(user)
    }
}

/** Actualizar un usuario
 *Permite modificar datos de un usuario existente.
 * Por ejemplo: actualizar nombre, foto o contraseña.
 */
class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: UserModel) {
        userRepository.updateUser(user)
    }
}

/** Eliminar un usuario
 *Elimina un usuario de la base de datos.
 * Se puede usar para logout de Google eliminando al usuario de Room.
 */

class DeleteUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: UserModel) {
        userRepository.deleteUser(user)
    }
}

/** Obtener un usuario por email
// Permite buscar un usuario por su email.
// Útil para login o verificar si ya existe en Room.
 */
class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String): UserModel? {
        return userRepository.getUserByEmail(email)
    }
}

/** Obtener el usuario actualmente logueado
 *Devuelve el usuario que tiene la sesión activa.
 * Se usa en SplashScreen o para refrescar estado en la app.
 */
class GetLoggedInUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): UserModel? {
        return userRepository.getLoggedInUser()
    }
}

/** Marcar un usuario como logueado
 *Cambia el estado de sesión de un usuario a activo.
 * Se llama luego de login exitoso.
 */
class SetUserLoggedInUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String) {
        userRepository.setUserLoggedIn(email)
    }
}

/** Limpiar sesión de todos los usuarios
 *  Cierra sesión de todos los usuarios.
 *  Se usa en logout local.
 */
class ClearSessionUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() {
        userRepository.clearSession()
    }
}

/**
 * Caso de uso unificado para login
 * Puede manejar login local (email/password) o login con Google (idToken)
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Realiza login según los parámetros recibidos
     * @param email email del usuario (opcional si es Google)
     * @param password contraseña del usuario (opcional si es Google)
     * @param idToken token de Google (opcional si es login local)
     * @return true si el login fue exitoso, false en caso contrario
     */
    suspend operator fun invoke(
        email: String? = null,
        password: String? = null,
        idToken: String? = null
    ): Boolean {
        return if (idToken != null) {
            authRepository.loginWithGoogle(idToken)
        } else if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            authRepository.login(email, password)
        } else {
            false
        }
    }
}


/**Logout completo
 * Cierra sesión de Firebase, Google y Room de manera segura.
 * Se llama desde Home o cualquier pantalla que permita cerrar sesión.
 */
class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}

/**
 *  Obtener usuario actual
 * Devuelve el usuario que actualmente está logueado (Room o Firebase).
 */
class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): UserModel? {
        return authRepository.getCurrentUser()
    }
}
