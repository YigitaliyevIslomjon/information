package uz.info.information.Service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import uz.info.information.*

interface AuthService {
    fun signIn(): Result
    fun signUp(dto: UserDto): Result
}


@Service
class AuthServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private var userRepository: UserRepository,
) : AuthService {
    override fun signIn(): Result = Result("user authenticated successfully")
    override fun signUp(dto: UserDto): Result = dto.run {
        val password = passwordEncoder.encode(dto.password)
        val username = userRepository.findByUsername(dto.username)
        if (username != null) {
            throw UsernameExistException("username ${dto.username} is already exist, choose another username")
        }
        userRepository.save(
            User(
                dto.firstName,
                dto.lastName,
                password,
                dto.username,
            )
        )
        return Result("data are saved successfully")
    }
}