package fi.hsl.transitdata.steps

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

class CheckEkeFile : TestStepCode {
    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        Thread.sleep(25000)

        val dir = Paths.get("testdata/eke")

        assertTrue(Files.list(dir).toList().isNotEmpty())

        val csvFile = Files.list(dir).findAny().get()
        val fileLines = GZIPInputStream(Files.newInputStream(csvFile)).bufferedReader(StandardCharsets.UTF_8).use { it.readLines() }

        assertEquals(2, fileLines.size)
    }
}