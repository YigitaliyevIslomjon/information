package uz.info.information.Service

import org.springframework.security.core.userdetails.UsernameNotFoundException
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
        userRepository.findByUsername(dto.username)
            ?: throw UsernameNotFoundException("username ${dto.username} is not found")

        userRepository.save(
            User(
                dto.firstName,
                dto.lastName,
                password,
                dto.username,
            )
        )
        return Result("data are saved")
    }
}