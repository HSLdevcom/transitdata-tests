package fi.hsl.transitdata.steps.metro

import org.apache.pulsar.client.admin.PulsarAdmin
import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

class CheckMetroPulsarStats : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val pulsar = containers["pulsar"]!!

        val pulsarAdminUrl = "http://${pulsar.host}:${pulsar.getMappedPort(8080)}"

        val pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl(pulsarAdminUrl).build()

        val messagesPublished = pulsarAdmin.topics().getStats("metro-estimate").msgOutCounter
        assertTrue(messagesPublished > 0)
    }
}