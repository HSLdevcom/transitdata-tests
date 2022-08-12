package fi.hsl.transitdata.steps.metro

import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object MetroTimeUtils {
    val dateTime by lazy {
        Instant.now().atOffset(ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES).toLocalDateTime()!!
    }
}