package uz.info.information

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class FileGraphicScheduler(private val fileGraphicRepository: FileGraphicRepository) {

    @Scheduled(fixedRate = 1000 * 60 * 60)
    fun updateStatusBasedOnLocalDate(){
        val fileGraphics = fileGraphicRepository.findAll()
        val currentDate = LocalDate.now()

        for (fileGraphic in fileGraphics){
            if(!fileGraphic.localDate.isAfter(currentDate)){
                fileGraphic.status = FileStatus.DONE
                fileGraphicRepository.save(fileGraphic)
            }
        }
    }
}