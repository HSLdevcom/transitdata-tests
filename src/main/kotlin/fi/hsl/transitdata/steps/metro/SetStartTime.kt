package fi.hsl.transitdata.steps.metro

import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SetStartTime : ParametrizedTestStepCode {
    companion object {
        const val START_TIME_KEY = "metro_start_time"
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val startTime = parameters["start-time"]

        val startTimeLocalDateTime = if (startTime == "now") {
            Instant.now().atOffset(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES).toLocalDateTime()!!
        } else {
            LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

        updateState(START_TIME_KEY) { startTimeLocalDateTime!! }
    }
}