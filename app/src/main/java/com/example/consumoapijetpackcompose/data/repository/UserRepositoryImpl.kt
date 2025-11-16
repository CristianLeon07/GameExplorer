package com.example.consumoapijetpackcompose.data.repository

import com.example.consumoapijetpackcompose.data.local.dao.UserDao
import com.example.consumoapijetpackcompose.data.local.entity.UserEntity
import com.example.consumoapijetpackcompose.domain.models.UserModel
import com.example.consumoapijetpackcompose.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    // ------------------------
    // Observables de usuarios
    // ------------------------

    /**
     * Flujo que emite la lista de todos los usuarios en la base de datos
     */
    override val users: Flow<List<UserModel>> = userDao.getUsers().map { list ->
        list.map { it.toModel() }
    }

    // ------------------------
    // CRUD de usuarios
    // ------------------------

    /**
     * Agrega un nuevo usuario a la base de datos
     */
    override suspend fun addUser(user: UserModel) {
        val entity = user.toEntity().copy(isLoggedIn = false) // por defecto no logueado
        userDao.addUser(entity)
    }

    /**
     * Actualiza un usuario existente
     */
    override suspend fun updateUser(user: UserModel) = userDao.updateUser(user.toEntity())

    /**
     * Elimina un usuario
     */
    override suspend fun deleteUser(user: UserModel) = userDao.deleteUser(user.toEntity())

    /**
     * Obtiene un usuario por email
     */
    override suspend fun getUserByEmail(email: String): UserModel? =
        userDao.getUserByEmail(email)?.toModel()

    // ------------------------
    // Sesión de usuario
    // ------------------------

    /**
     * Obtiene el usuario actualmente logueado
     */
    override suspend fun getLoggedInUser(): UserModel? = userDao.getLoggedInUser()?.toModel()

    /**
     * Marca un usuario como logueado y desmarca todos los demás
     */
    override suspend fun setUserLoggedIn(email: String) {
        userDao.clearAllSessions()
        userDao.setUserLoggedIn(email)
    }

    /**
     * Limpia todas las sesiones activas
     */
    override suspend fun clearSession() = userDao.clearAllSessions()

    // ------------------------
    // Mapeo Entity <-> Model
    // ------------------------

    /**
     * Convierte UserEntity a UserModel
     */
    private fun UserEntity.toModel() = UserModel(
        email = email,
        name = name,
        password = password ?: "",
        confPassword = null,
        loginMethod = loginMethod,
        isLoggedIn = isLoggedIn,
        photo = photo
    )

    /**
     * Convierte UserModel a UserEntity
     */
    private fun UserModel.toEntity() = UserEntity(
        email = email,
        name = name,
        password = password,
        loginMethod = loginMethod,
        isLoggedIn = isLoggedIn,
        photo = photo
    )
}


