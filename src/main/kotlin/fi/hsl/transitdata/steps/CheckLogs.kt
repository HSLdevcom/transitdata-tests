package fi.hsl.transitdata.steps

import org.junit.jupiter.api.Assertions.assertTrue
import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.ParametrizedTestStepCode

/**
 * Test step that checks whether logs contain certain message
 */
class CheckLogs : ParametrizedTestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        parameters: Map<String, String>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val containerName = parameters["container"]!!
        val messageRegex = Regex(parameters["message"]!!)

        val container = containers[containerName]!!

        assertTrue { messageRegex.containsMatchIn(container.logs) }
    }
}