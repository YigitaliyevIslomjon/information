package uz.info.information


import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(baseException: BaseException): ResponseEntity<*> {
        return ResponseEntity.badRequest().body(baseException.getModel())
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFoundException(ex: NoHandlerFoundException): ResponseEntity<String> {
        return ResponseEntity("This URL does not exist", HttpStatus.NOT_FOUND)
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    protected fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?
    ): ResponseEntity<Any> {
        val errorMessage = "Unsupported media type. Please use a valid media type."
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ErrorMessageModel(message = errorMessage, status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): Map<String, String> {
        println(ex.message)
        val errors = ex.bindingResult.allErrors.map { error -> error.defaultMessage }
        return mapOf("errors" to errors.joinToString(", "))
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.FORBIDDEN.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorMessage)
    }
}

abstract class BaseException(private val msg: String? = null) : RuntimeException(msg) {
    abstract fun errorCode(): ErrorCode
    abstract fun getModel(): ErrorMessageModel
}

class UsernameExistException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.USERNAME_EXIST
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class FileNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.FILE_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class FileGraphicNotException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.FILE_GRAPHIC_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class GraphicTimeNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.GRAPHIC_TIME_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class LocalDateAndGraphicTimeMustBeUniqueException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.LOCAL_DATE_AND_GRAPHIC_TIME_MUST_BE_UNIQUE
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null
)