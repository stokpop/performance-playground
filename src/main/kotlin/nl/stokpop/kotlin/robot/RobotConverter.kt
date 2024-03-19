package nl.stokpop.kotlin.robot

import com.fasterxml.jackson.core.JsonProcessingException
import kotlin.Throws
import com.fasterxml.jackson.databind.ObjectMapper
import nl.stokpop.robot.domain.Robot

object RobotConverter {
    @JvmStatic
    @Throws(JsonProcessingException::class)
    fun convertRobot(robot: Robot?): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(robot)
    }
}