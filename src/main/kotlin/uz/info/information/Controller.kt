package uz.info.information

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import uz.info.information.Service.AuthService
import uz.info.information.Service.FileAttachmentService
import uz.info.information.Service.FileGraphicService
import uz.info.information.Service.GraphicTimeService
import java.time.LocalDate
import java.util.UUID

const val BASE_PREFIX = "api/v1"

@RestController
@RequestMapping("${BASE_PREFIX}/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("sign-in")
    fun login(): Result  = authService.signIn()

    @PostMapping("sign-up")
    fun userRegister(@Valid @RequestBody dto: UserDto): Result = authService.signUp(dto)

}

@RestController
@RequestMapping("${BASE_PREFIX}/graphic-time")
class GraphicTimeController(
    private val graphicTimeService: GraphicTimeService
) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: GraphicTimeDto) = graphicTimeService.add(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = graphicTimeService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): GraphicTimeDtoResponse = graphicTimeService.getOne(id)

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: GraphicTimeDto): Result = graphicTimeService.edit(id, dto)

    @GetMapping("pageable")
    fun getAll(
        pageable: Pageable
    ): Page<GraphicTimeDtoResponse> = graphicTimeService.getAll(pageable)
}

@RestController
@RequestMapping("${BASE_PREFIX}/file-graphic")
class FileGraphicController(
    private val fileGraphicService: FileGraphicService,
) {

    @PostMapping("/add")
    fun add(
        @Valid @RequestBody dto: FileGraphicDto,
    ) = fileGraphicService.add(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = fileGraphicService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): FileGraphicDtoResponse = fileGraphicService.getOne(id)

    @PutMapping("{id}")
    fun edit(
        @PathVariable id: Long,
        @Valid @RequestBody dto: FileGraphicDto,
    ): Result = fileGraphicService.edit(id, dto)

    @GetMapping("pageable")
    fun getAll(
        @RequestParam("status") fileStatus: FileStatus,
        @RequestParam("startDate") startDate: LocalDate,@RequestParam("endDate") endDate:LocalDate,
        pageable: Pageable
    ): Page<FileGraphicDtoResponse> = fileGraphicService.getAll(startDate,endDate, fileStatus, pageable)

    @PutMapping
    fun changeStatus(id: Long, status: FileStatus): Result = fileGraphicService.changeStatus(id, status)
}

@RestController
@RequestMapping("${BASE_PREFIX}/file")
class FileAttachmentController(
    private val fileAttachmentService: FileAttachmentService,
) {

    @PostMapping("/add")
    fun add(
        request: MultipartHttpServletRequest
    ) = fileAttachmentService.add(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: UUID, request: HttpServletResponse) = fileAttachmentService.getOne(id, request)

}
