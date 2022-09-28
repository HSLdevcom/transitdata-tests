package fi.hsl.transitdata

import xyz.malkki.microservicetest.testexecution.MicroserviceTestExecutor
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
    MicroserviceTestExecutor.Companion.runMicroserviceTests()
}