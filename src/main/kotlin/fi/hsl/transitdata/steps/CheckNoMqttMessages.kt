package fi.hsl.transitdata.steps

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.jupiter.api.Assertions
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class CheckNoMqttMessages : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(10000)

        val mqttMessages =
            getState(StartMqttListener.MQTT_MESSAGES_STATE_KEY)?.let { it as List<Pair<String, MqttMessage>> }
                ?.toList() ?: emptyList<List<Pair<String, MqttMessage>>>()

        Assertions.assertTrue(mqttMessages.isEmpty(), "MQTT message list is not empty")
    }
}