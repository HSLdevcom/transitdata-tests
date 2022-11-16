package fi.hsl.transitdata.steps.apc

import org.eclipse.paho.client.mqttv3.MqttClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SendMockData : TestStepCode {
    private val HFP_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

    private fun sendMockApc(mqttHost: String, timestamp: OffsetDateTime) {
        val mqttClient = MqttClient(mqttHost, MqttClient.generateClientId())
        mqttClient.connect()

        mqttClient.publish("/hfp/v2/journey/ongoing/apc/bus/0022/01250", "{ \"APC\": {\"desi\": \"20\", \"dir\": \"1\", \"oper\": \"22\", \"veh\": \"1250\", \"tst\": \"${HFP_TIMESTAMP_FORMAT.format(timestamp)}\", \"tsi\": ${timestamp.toEpochSecond()}, \"lat\": \"60.202890\", \"long\": \"24.881890\", \"odo\": \"7234\", \"oday\": \"2022-10-18\", \"jrn\": \"2098\", \"line\": \"51\", \"start\": \"15:01\", \"loc\": \"ODO\", \"stop\": \"1304127\", \"route\": \"1020\", \"vehiclecounts\": {\"countquality\": \"regular\", \"vehicleload\": 54, \"vehicleloadratio\": \"0.76\", \"doorcounts\": [{\"door\": \"1\", \"count\": [{\"class\": \"adult\", \"in\": 0, \"out\": 0}]},{\"door\": \"2\", \"count\": [{\"class\": \"adult\", \"in\": 1, \"out\": 3}]},{\"door\": \"3\", \"count\": [{\"class\": \"adult\", \"in\": 2, \"out\": 2}]}]}}}\n".toByteArray(StandardCharsets.UTF_8), 0, false)

        mqttClient.disconnect(1000)
        mqttClient.close(true)
    }

    private fun sendMockHfp(mqttHost: String, timestamp: OffsetDateTime) {
        val hfpMsg = """
            {
              "VP": {
                "desi": "20",
                "dir": "1",
                "oper": 22,
                "veh": 1250,
                "tst": "${HFP_TIMESTAMP_FORMAT.format(timestamp)}",
                "tsi": ${timestamp.toEpochSecond()},
                "spd": 12.5,
                "hdg": 354,
                "lat": 24.9435,
                "long": 60.1967,
                "acc": -12.34,
                "dl": 600,
                "odo": 45.12,
                "drst": 0,
                "oday": "2022-10-18",
                "jrn": 1,
                "line": 264,
                "start": "15:01",
                "loc": "GPS",
                "stop": null,
                "route": "1020",
                "occu": 0
              }
            }
        """.trimIndent()

        val hfpTopic = "/hfp/v2/journey/ongoing/vp/bus/0022/01250/1020/1/Test/15:01/1130106/2/60;24/19/94/63"

        val mqttClient = MqttClient(mqttHost, MqttClient.generateClientId())
        mqttClient.connect()

        mqttClient.publish(hfpTopic, hfpMsg.toByteArray(StandardCharsets.UTF_8), 0, false)

        mqttClient.disconnect(1000)
        mqttClient.close(true)
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val timestamp = Instant.now().atOffset(ZoneOffset.UTC)

        val apcMosquitto = containers["apc_mosquitto"]!!

        sendMockApc("tcp://${apcMosquitto.host}:${apcMosquitto.firstMappedPort}", timestamp)

        val mosquitto = containers["mosquitto"]!!

        sendMockHfp("tcp://${mosquitto.host}:${mosquitto.firstMappedPort}", timestamp)
    }
}