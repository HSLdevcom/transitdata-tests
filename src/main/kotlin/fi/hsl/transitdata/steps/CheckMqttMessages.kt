package fi.hsl.transitdata.steps

import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode

private val log = KotlinLogging.logger{}

class CheckMqttMessages : ParametrizedTestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(10000)

        val listenerName = parameters["name"]

        val mqttMessages = (getState(StartMqttListener.getListenerSpecificKey(StartMqttListener.MQTT_MESSAGES_STATE_KEY, listenerName))?.let { it as List<Pair<String, MqttMessage>> }?.toList() ?: emptyList<List<Pair<String, MqttMessage>>>())

        log.info { "MQTT messages: ${mqttMessages.size}" }

        assertTrue(mqttMessages.isNotEmpty(), "MQTT message list is empty")
    }
}