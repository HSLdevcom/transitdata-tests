package fi.hsl.transitdata.steps

import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode

class CheckAndRemoveFirstMqttJsonObjectMessage : ParametrizedTestStepCode {
    private val log = KotlinLogging.logger {}

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(10000)
        val mqttMessages =
            (getState(StartMqttListener.MQTT_MESSAGES_STATE_KEY)?.let { it as List<Pair<String, MqttMessage>> }
                ?.toList()
                ?: emptyList<List<Pair<String, MqttMessage>>>()) as List<Pair<String, MqttMessage>>
        log.info { "MQTT messages: ${mqttMessages.size}" }
        Assertions.assertTrue(mqttMessages.isNotEmpty(), "MQTT message list is empty")
        updateState(StartMqttListener.MQTT_MESSAGES_STATE_KEY) {
            (it as MutableList<Pair<String, MqttMessage>>).removeFirst()
            return@updateState it
        }

        val topicAndPayload = mqttMessages.first()
        val topic = topicAndPayload.first
        val message = topicAndPayload.second
        Assertions.assertEquals(parameters["expected-topic"]!!, topic)
        Assertions.assertEquals(
            JSONObject(parameters["expected-payload"]!!).toString(),
            JSONObject(message.payload.toString(Charsets.UTF_8)).toString()
        )
    }
}