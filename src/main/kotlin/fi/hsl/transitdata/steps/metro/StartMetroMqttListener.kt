package fi.hsl.transitdata.steps.metro

import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.util.*

private val log = KotlinLogging.logger {}

//TODO: create generic MQTT listener which takes topic name as parameter
class StartMetroMqttListener : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(10 * 1000)

        updateState("mqtt-messages") { Collections.synchronizedList(mutableListOf<Pair<String, MqttMessage>>()) }

        val mosquitto = containers["mosquitto"]!!

        val url = "tcp://${mosquitto.host}:${mosquitto.firstMappedPort}"

        val mqttClient = MqttAsyncClient(url, "mqtt-async-client")
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable) {
                log.warn { "Lost connection to MQTT broker: ${cause.message}" }
                cause.printStackTrace()
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                updateState("mqtt-messages") {
                    (it as MutableList<Pair<String, MqttMessage>>).add(topic to message)
                    return@updateState it
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                log.info { "Connected to MQTT" }
                mqttClient.subscribe("gtfsrt/#", 0)
            }
        })
        try {
            mqttClient.connect().waitForCompletion()
        } catch (e: Exception) {
            e.printStackTrace()
            log.info { "MQTT connection failed" }
        }

        updateState("mqtt-client") { mqttClient }
    }
}