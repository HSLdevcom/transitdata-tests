package fi.hsl.transitdata.steps.metro

import com.google.transit.realtime.GtfsRealtime
import fi.hsl.transitdata.steps.StartMqttListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class CheckMetroMqttMessage : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(5000)

        val mqttMessages = (getState(StartMqttListener.MQTT_MESSAGES_STATE_KEY)?.let { it as List<Pair<String, MqttMessage>> }?.toList() ?: emptyList<Pair<String, MqttMessage>>())

        assertTrue(mqttMessages.isNotEmpty(), "MQTT message list is empty")

        val firstMessage = mqttMessages.first()

        val gtfsRtFeedMessage = GtfsRealtime.FeedMessage.parseFrom(firstMessage.second.payload)
        val gtfsRtFeedEntities = gtfsRtFeedMessage.entityList

        assertTrue(gtfsRtFeedEntities.isNotEmpty(), "GTFS-RT feed entity list is empty")

        val firstEntity = gtfsRtFeedEntities.first()

        assertTrue(firstEntity.hasTripUpdate(), "First GTFS-RT feed entity does not contain trip update")

        val tripUpdate = firstEntity.tripUpdate
        val stopTimeUpdates = tripUpdate.stopTimeUpdateList

        assertTrue(stopTimeUpdates.isNotEmpty(), "Trip update stop time update list is empty")

        val firstStopId = stopTimeUpdates.first().stopId

        assertEquals("2413601", firstStopId)
    }
}