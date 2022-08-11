package fi.hsl.transitdata.steps.eke

import org.eclipse.paho.client.mqttv3.*
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class SendEkeMqtt : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val ekeData = SendEkeMqtt::class.java.classLoader.getResourceAsStream("stadlerUDP.dat").readBytes()

        val mosquitto = containers["mosquitto"]!!

        val url = "tcp://${mosquitto.host}:${mosquitto.firstMappedPort}"

        val mqttClient = MqttClient(url, MqttClient.generateClientId())

        try {
            mqttClient.connect()

            mqttClient.publish("eke/v1/sm5/15/A/stadlerUDP", ekeData, 1, false)

            mqttClient.disconnect(10000)
            mqttClient.close(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}