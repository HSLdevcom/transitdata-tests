package fi.hsl.transitdata.steps

import mu.KotlinLogging
import org.apache.pulsar.client.admin.PulsarAdmin
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode

private val log = KotlinLogging.logger {}

class PrintPulsarStats : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val pulsar = containers["pulsar"]!!

        val pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl("http://${pulsar.host}:${pulsar.getMappedPort(8080)}").build()

        val logMsg = pulsarAdmin.namespaces().getNamespaces("public")
            .flatMap { pulsarAdmin.namespaces().getTopics(it) }
            .map { it to pulsarAdmin.topics().getStats(it) }
            .joinToString("\n") { (topic, stats) -> "$topic - messages in ${stats.msgInCounter}, messages out: ${stats.msgOutCounter}" }

        log.info { "Stats for Pulsar topics:\n$logMsg" }
    }
}