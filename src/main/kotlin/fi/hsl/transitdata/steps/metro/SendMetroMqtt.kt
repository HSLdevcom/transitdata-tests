package fi.hsl.transitdata.steps.metro

import org.eclipse.paho.client.mqttv3.MqttClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendMetroMqtt : TestStepCode {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

    private fun generateTime(seq: Int): OffsetDateTime {
        return MetroTimeUtils.dateTime.atOffset(ZoneOffset.UTC).plusMinutes(seq.toLong())
    }

    private fun generateRouteRowTimes(seq: Int): String {
        val time = generateTime(seq)

        return """
              "arrivalTimePlanned": "${dateTimeFormatter.format(time)}",
              "arrivalTimeForecast": "${dateTimeFormatter.format(time)}",
              "arrivalTimeMeasured": "null",
              "departureTimePlanned": "${dateTimeFormatter.format(time)}",
              "departureTimeForecast": "${dateTimeFormatter.format(time)}",
              "departureTimeMeasured": "null",
        """.trimIndent()
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val metroData = """
            {
              "routeName": "FIN-VS",
              "trainType": "M",
              "journeySectionprogress": "INPROGRESS",
              "beginTime": "${dateTimeFormatter.format(generateTime(-1))}",
              "endTime": "${dateTimeFormatter.format(generateTime(21))}",
              "routeRows": [
                {
                  "routerowId": 25866851,
                  "station": "FIN",
                  "platform": "1",
                  ${generateRouteRowTimes(-1)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866852,
                  "station": "MAK",
                  "platform": "1",
                  ${generateRouteRowTimes(0)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866853,
                  "station": "NIK",
                  "platform": "1",
                  ${generateRouteRowTimes(1)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866854,
                  "station": "URP",
                  "platform": "1",
                  ${generateRouteRowTimes(2)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866855,
                  "station": "TAP",
                  "platform": "1",
                  ${generateRouteRowTimes(3)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866856,
                  "station": "OTA",
                  "platform": "1",
                  ${generateRouteRowTimes(4)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866857,
                  "station": "KEN",
                  "platform": "1",
                  ${generateRouteRowTimes(5)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866858,
                  "station": "KOS",
                  "platform": "1",
                  ${generateRouteRowTimes(6)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866859,
                  "station": "LAS",
                  "platform": "1",
                  ${generateRouteRowTimes(7)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866860,
                  "station": "RL",
                  "platform": "1",
                  ${generateRouteRowTimes(8)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866861,
                  "station": "KP",
                  "platform": "1",
                  ${generateRouteRowTimes(9)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866862,
                  "station": "RT",
                  "platform": "1",
                  ${generateRouteRowTimes(10)}
                  "source": "HASTUS",
                  "rowProgress": "COMPLETED"
                },
                {
                  "routerowId": 25866863,
                  "station": "HY",
                  "platform": "1",
                  ${generateRouteRowTimes(11)}
                  "source": "HASTUS",
                  "rowProgress": "INPROGRESS"
                },
                {
                  "routerowId": 25866864,
                  "station": "HT",
                  "platform": "1",
                  ${generateRouteRowTimes(12)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866865,
                  "station": "SN",
                  "platform": "1",
                  ${generateRouteRowTimes(13)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866866,
                  "station": "KA",
                  "platform": "1",
                  ${generateRouteRowTimes(14)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866867,
                  "station": "KS",
                  "platform": "1",
                  ${generateRouteRowTimes(15)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866868,
                  "station": "HN",
                  "platform": "1",
                  ${generateRouteRowTimes(16)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866869,
                  "station": "ST",
                  "platform": "1",
                  ${generateRouteRowTimes(17)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866870,
                  "station": "IK",
                  "platform": "1",
                  ${generateRouteRowTimes(18)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866871,
                  "station": "PT",
                  "platform": "1",
                  ${generateRouteRowTimes(19)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866872,
                  "station": "RS",
                  "platform": "1",
                  ${generateRouteRowTimes(20)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                },
                {
                  "routerowId": 25866873,
                  "station": "VS",
                  "platform": "1",
                  ${generateRouteRowTimes(21)}
                  "source": "HASTUS",
                  "rowProgress": "SCHEDULED"
                }
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