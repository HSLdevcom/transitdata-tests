package fi.hsl.transitdata.steps

import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class StopMqttListener : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val mqttClient = (getState("mqtt-client") as MqttAsyncClient)
        mqttClient.disconnectForcibly(1000, 1000)
        mqttClient.close(true)
    }
}