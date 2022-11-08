package fi.hsl.transitdata.steps.metro

import mu.KotlinLogging
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.Jedis
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

class AddMetroDataToRedis : ParametrizedTestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val redis = containers["redis"]!!

        val startStopNumber = parameters["start-stop-number"]

        val startTime = (getState(SetStartTime.START_TIME_KEY) as LocalDateTime).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Helsinki"))

        val date = startTime.toLocalDate()
        val dateFormatted = date.format(DateTimeFormatter.BASIC_ISO_DATE)

        val time = startTime.toLocalTime()
        val timeFormatted = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val dateTimeFormatted = date.atTime(time).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

        val jedis = Jedis(redis.host, redis.firstMappedPort)
        jedis.use {
            jedis.connect()

            val key = "metro:${startStopNumber}_${dateTimeFormatted}"
            val values = mapOf(
                "djv-id" to "1",
                "route-name" to "31M1",
                "direction" to "1",
                "start-time" to timeFormatted,
                "operating-day" to dateFormatted,
                "start-datetime" to dateTimeFormatted,
                "start-stop-number" to startStopNumber
            )

            log.info { "Adding metro data to Redis, key: $key, values: $values" }

            jedis.hset(key, values)

            //jedis.set("1-1-20220726-12:00:00", "1")

            jedis.set("cache-update-ts", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        }
    }
}