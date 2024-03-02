package uz.info.information.Service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.info.information.*

interface GraphicTimeService {
    fun add(dto: GraphicTimeDto): Result
    fun edit(id: Long, dto: GraphicTimeDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): GraphicTimeDtoResponse
    fun getAll(pageable: Pageable): Page<GraphicTimeDtoResponse>
}


@Service
class GraphicTimeServiceImpl(private val graphicTimeRepository: GraphicTimeRepository) : GraphicTimeService {
    override fun add(dto: GraphicTimeDto): Result = dto.run {
        graphicTimeRepository.save(
            GraphicTime(
                time
            )
        )
        Result(message = "date are saved")
    }

    override fun edit(id: Long, dto: GraphicTimeDto): Result = dto.run {

        val existingGraphicTime = graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")

        graphicTimeRepository.save(existingGraphicTime.apply {
            time = dto.time
        }
        )
        Result(message = "date are edit successfully")
    }

    override fun delete(id: Long): Result {
        graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")

        graphicTimeRepository.deleteById(id)
        return Result("data are deleted successfully")
    }

    override fun getOne(id: Long): GraphicTimeDtoResponse {
        val existingGraphicTime = graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")
        return GraphicTimeDtoResponse.toResponse(existingGraphicTime)
    }

    override fun getAll(pageable: Pageable): Page<GraphicTimeDtoResponse> {
        val existingGraphicTime = graphicTimeRepository.findAll(pageable)
        return existingGraphicTime.map(GraphicTimeDtoResponse.Companion::toResponse)

    }
}