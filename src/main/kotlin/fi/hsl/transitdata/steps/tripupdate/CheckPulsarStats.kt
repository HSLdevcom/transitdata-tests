package fi.hsl.transitdata.steps.tripupdate

import org.apache.pulsar.client.admin.PulsarAdmin
import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class CheckPulsarStats : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(5000)

        val pulsar = containers["pulsar"]!!

        val pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl("http://${pulsar.host}:${pulsar.getMappedPort(8080)}").build()

        assertTrue(pulsarAdmin.topics().getStats("persistent://public/default/gtfsrt_tripupdate").msgInCounter > 0)
    }
}