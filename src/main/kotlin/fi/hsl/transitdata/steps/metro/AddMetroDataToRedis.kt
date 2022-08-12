package fi.hsl.transitdata.steps.metro

import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.Jedis
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AddMetroDataToRedis : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val redis = containers["redis"]!!

        val date = MetroTimeUtils.dateTime.toLocalDate()
        val dateFormatted = date.format(DateTimeFormatter.BASIC_ISO_DATE)

        val time = MetroTimeUtils.dateTime.toLocalTime()
        val timeFormatted = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val dateTimeFormatted = date.atTime(time).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)

        val jedis = Jedis(redis.host, redis.firstMappedPort)
        jedis.use {
            jedis.connect()

            jedis.hset("metro:MAK_${dateTimeFormatted}", mapOf(
                "djv-id" to "1",
                "route-name" to "31M1",
                "direction" to "1",
                "start-time" to timeFormatted,
                "operating-day" to dateFormatted,
                "start-datetime" to dateTimeFormatted,
                "start-stop-number" to "2314601"
            ))

            jedis.set("1-1-20220726-12:00:00", "1")

            jedis.set("cache-update-ts", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        }
    }
}