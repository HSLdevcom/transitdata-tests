package fi.hsl.transitdata.steps

import org.eclipse.paho.client.mqttv3.MqttClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode

class SendMqttMessage : ParametrizedTestStepCode {
    private fun sendMqttMessage(host: String, topic: String, payload: ByteArray) {
        val mqttClient = MqttClient(host, MqttClient.generateClientId())
        mqttClient.connect()
        mqttClient.publish(topic, payload, 1, false)
        mqttClient.disconnect(1000)
        mqttClient.close(true)
    }

    private fun readPayloadFromResource(resourceName: String): ByteArray {
        return SendMqttMessage::class.java.classLoader.getResourceAsStream(resourceName).readBytes()
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val broker = containers[parameters["mqtt-broker-container"]!!]!!

        val payload = parameters["payload"]?.toByteArray(Charsets.UTF_8) ?: readPayloadFromResource(parameters["payload-resource"]!!)

        sendMqttMessage(
            "tcp://${broker.host}:${broker.firstMappedPort}",
            parameters["topic"]!!,
            payload
        )
    }
}