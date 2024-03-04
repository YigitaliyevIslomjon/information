package uz.info.information.Service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.info.information.*

interface GraphicTimeService {
    fun add(dto: GraphicTimeDto): Result
    fun edit(id: Long, dto: GraphicTimeDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): GraphicTimeDtoResponse
    fun getAll(): List<GraphicTimeDtoResponse>
}

@Service
class GraphicTimeServiceImpl(private val graphicTimeRepository: GraphicTimeRepository) : GraphicTimeService {
    override fun add(dto: GraphicTimeDto): Result {
        val graphicTime = graphicTimeRepository.findByTime(dto.time)

        if (graphicTime != null && graphicTime.delete) {
            graphicTimeRepository.save(graphicTime.apply { this.delete = false })
            return Result(message = "date are saved")
        }else if (graphicTime != null) {
            throw GraphicTimeAlreadyExistException("this time ${dto.time} is already exist")
        }

        graphicTimeRepository.save(
            GraphicTime(
                dto.time
            )
        )
        return Result(message = "date are saved successfully")
    }

    override fun edit(id: Long, dto: GraphicTimeDto): Result = dto.run {

        val graphicTime = graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")

        if (time != graphicTime.time) {
            val isExist = graphicTimeRepository.existsByTime(dto.time)
            if (!isExist) {
                graphicTimeRepository.save(graphicTime.apply {
                    this.time = dto.time
                })
            } else {
                throw GraphicTimeAlreadyExistException("this time ${dto.time} is already exist, change time")
            }
        }

        Result(message = "date are edit successfully")
    }

    override fun delete(id: Long): Result {
       val graphicTime =  graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")

        graphicTimeRepository.save(graphicTime.apply { this.delete = true })
        return Result("data are deleted successfully")
    }

    override fun getOne(id: Long): GraphicTimeDtoResponse {
        val existingGraphicTime = graphicTimeRepository.findByIdOrNull(id)
            ?: throw GraphicTimeNotFoundException("GraphicTime id $id is not found")
        return GraphicTimeDtoResponse.toResponse(existingGraphicTime)
    }

    override fun getAll() : List<GraphicTimeDtoResponse> {
        val existingGraphicTime = graphicTimeRepository.getAllTime()
        return existingGraphicTime.map(GraphicTimeDtoResponse.Companion::toResponse)
    }
}