package fi.hsl.transitdata.steps.tripupdate

import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.Jedis
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddDoiDataToRedis : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val redis = containers["redis"]!!

        val date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val time = LocalTime.now().minusHours(1).truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val jedis = Jedis(redis.host, redis.firstMappedPort)
        jedis.use {
            jedis.connect()

            jedis.set("jpp:1", "1")
            jedis.set("jpp:2", "2")
            jedis.set("jpp:3", "3")
            jedis.set("jpp:4", "4")

            jedis.hset("dvj:1", mapOf(
                "route-name" to "1001",
                "direction" to "1",
                "start-time" to time,
                "operating-day" to date
            ))

            jedis.set("1-1-20220726-12:00:00", "1")

            jedis.set("cache-update-ts", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        }
    }
}