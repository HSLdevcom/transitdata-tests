package fi.hsl.transitdata.steps

import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.*
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode
import java.util.*

private val log = KotlinLogging.logger {}

class StartMqttListener : ParametrizedTestStepCode {
    companion object {
        const val MQTT_MESSAGES_STATE_KEY = "mqtt-messages"
        const val MQTT_CLIENT_STATE_KEY = "mqtt-client"

        fun getListenerSpecificKey(key: String, listenerName: String?): String {
            if (listenerName.isNullOrBlank()) {
                return key
            } else {
                return "${key}_$listenerName"
            }
        }
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val listenerName = parameters["name"]

        updateState(getListenerSpecificKey(MQTT_MESSAGES_STATE_KEY, listenerName)) { Collections.synchronizedList(mutableListOf<Pair<String, MqttMessage>>()) }

        val containerName = parameters["mqtt-broker-container"]
        val mqttBroker = containers[containerName]!!

        val url = "tcp://${mqttBroker.host}:${mqttBroker.firstMappedPort}"

        val mqttClient = MqttAsyncClient(url, MqttAsyncClient.generateClientId())
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable) {
                log.warn { "Lost connection to MQTT broker: ${cause.message}" }
                cause.printStackTrace()
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                updateState(getListenerSpecificKey(MQTT_MESSAGES_STATE_KEY, listenerName)) {
                    (it as MutableList<Pair<String, MqttMessage>>).add(topic to message)
                    return@updateState it
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                val topic = parameters["mqtt-topic"]

                log.info { "Connected to MQTT, subscribing topic: $topic" }
                mqttClient.subscribe(topic, 0)
            }
        })
        try {
            val mqttConnectOptions = MqttConnectOptions().apply {
                isCleanSession = true
                keepAliveInterval = 15
                connectionTimeout = 30
            }
            mqttClient.connect(mqttConnectOptions).waitForCompletion()
        } catch (e: Exception) {
            e.printStackTrace()
            log.info { "MQTT connection failed" }
        }

        updateState(getListenerSpecificKey(MQTT_CLIENT_STATE_KEY, listenerName)) { mqttClient }
    }
}