package fi.hsl.transitdata.steps

import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode

class StopMqttListener : ParametrizedTestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val listenerName = parameters["name"]

        val mqttClient = (getState(StartMqttListener.getListenerSpecificKey(StartMqttListener.MQTT_CLIENT_STATE_KEY, listenerName)) as MqttAsyncClient)
        mqttClient.disconnectForcibly(1000, 1000)
        mqttClient.close(true)
    }
}