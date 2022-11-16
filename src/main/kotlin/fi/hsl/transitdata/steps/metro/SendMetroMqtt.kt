package fi.hsl.transitdata.steps.metro

import org.eclipse.paho.client.mqttv3.MqttClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendMetroMqtt : ParametrizedTestStepCode {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

    private fun generateTime(startTime: LocalDateTime, seq: Int): OffsetDateTime {
        return startTime.atOffset(ZoneOffset.UTC).plusMinutes(seq.toLong())
    }

    private fun generateRouteRowTimes(startTime: LocalDateTime, seq: Int): String {
        val time = generateTime(startTime, seq)

        return """
              "arrivalTimePlanned": "${dateTimeFormatter.format(time)}",
              "arrivalTimeForecast": "${dateTimeFormatter.format(time)}",
              "arrivalTimeMeasured": "null",
              "departureTimePlanned": "${dateTimeFormatter.format(time)}",
              "departureTimeForecast": "${dateTimeFormatter.format(time)}",
              "departureTimeMeasured": "null",
        """.trimIndent()
    }

    private fun generateRouteRows(startTime: LocalDateTime, stations: List<String>, firstStation: String): String {
        val firstStationIndex = stations.indexOf(firstStation)

        return stations.mapIndexed { index, station ->
            val stationIndexDiffToFirst = index - firstStationIndex

            return@mapIndexed """
                {
                  "routerowId": ${stationIndexDiffToFirst},
                  "station": "$station",
                  "platform": "1",
                  ${generateRouteRowTimes(startTime, stationIndexDiffToFirst)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                }
        """.trimIndent()
        }.joinToString(",\n")
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val stations = parameters["stations"]!!.split(",").map { it.trim() }
        val firstStation = parameters["first-station"]!!
        val firstStationIndex = -stations.indexOf(firstStation)

        val startTime = getState(SetStartTime.START_TIME_KEY) as LocalDateTime

        val metroData = """
            {
              "routeName": "${stations.first()}-${stations.last()}",
              "trainType": "M",
              "journeySectionprogress": "INPROGRESS",
              "beginTime": "${dateTimeFormatter.format(generateTime(startTime, -firstStationIndex))}",
              "endTime": "${dateTimeFormatter.format(generateTime(startTime, stations.size - 1 - firstStationIndex))}",
              "routeRows": [
                ${generateRouteRows(startTime, stations, firstStation)}
              ]
            }

        """.trimIndent().encodeToByteArray()

        val mosquitto = containers["mosquitto"]!!

        val url = "tcp://${mosquitto.host}:${mosquitto.firstMappedPort}"

        val mqttClient = MqttClient(url, MqttClient.generateClientId())

        try {
            mqttClient.connect()

            mqttClient.publish("metro-schedule", metroData, 1, false)

            mqttClient.disconnect(10000)
            mqttClient.close(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}