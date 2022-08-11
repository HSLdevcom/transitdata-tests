package fi.hsl.transitdata.steps.tripupdate

import org.testcontainers.containers.GenericContainer
import xyz.malkki.microservicetest.testexecution.TestStepCode
import java.sql.DriverManager
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddRoiDataToDb : TestStepCode {
    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }

    override fun execute(
        containers: Map<String, GenericContainer<*>>,
        updateState: (key: String, updater: (Any?) -> Any) -> Unit,
        getState: (key: String) -> Any?
    ) {
        val postgres = containers["postgres"]!!

        val connString = "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/?user=postgres&password=password"

        val firstStopTime = ZonedDateTime.now(ZoneId.of("Europe/Helsinki")).truncatedTo(ChronoUnit.MINUTES).minusHours(1).toLocalDateTime()

        val lastModified = OffsetDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMATTER)

        DriverManager.getConnection(connString).use { connection ->
            connection.prepareStatement(
                """
                   CREATE TABLE Arrival (
                   	Id BIGINT NOT NULL,
                   	IsOnDatedVehicleJourneyId BIGINT NOT NULL,
                   	IsOnMonitoredVehicleJourneyId BIGINT NULL,
                   	JourneyPatternSequenceNumber INT NOT NULL,
                   	IsTimetabledAtJourneyPatternPointGid BIGINT NOT NULL,
                   	VisitCountNumber INT NOT NULL,
                   	IsTargetedAtJourneyPatternPointGid BIGINT NOT NULL,
                   	WasObservedAtJourneyPatternPointGid BIGINT NULL,
                   	TimetabledLatestDateTime DATE NOT NULL,
                   	TargetDateTime TIMESTAMP NOT NULL,
                   	EstimatedDateTime TIMESTAMP NULL,
                   	ObservedDateTime TIMESTAMP NULL,
                   	State INT NOT NULL,
                   	Type INT NOT NULL,
                   	IsValidYesNo INT NOT NULL,
                   	LastModifiedUTCDateTime TIMESTAMP NULL
                   );
                   
                   CREATE TABLE Departure (
                   	Id BIGINT NOT NULL,
                   	IsOnDatedVehicleJourneyId BIGINT NOT NULL,
                   	IsOnMonitoredVehicleJourneyId BIGINT NULL,
                   	JourneyPatternSequenceNumber INT NOT NULL,
                   	IsTimetabledAtJourneyPatternPointGid BIGINT NOT NULL,
                   	VisitCountNumber INT NOT NULL,
                   	IsTargetedAtJourneyPatternPointGid BIGINT NULL,
                   	WasObservedAtJourneyPatternPointGid BIGINT NULL,
                   	HasDestinationDisplayId BIGINT NULL,
                   	HasDestinationStopAreaGid BIGINT NULL,
                   	HasServiceRequirementId BIGINT NULL,
                   	TimetabledEarliestDateTime TIMESTAMP NOT NULL,
                   	TargetDateTime TIMESTAMP NOT NULL,
                   	EstimatedDateTime TIMESTAMP NULL,
                   	ObservedDateTime TIMESTAMP NULL,
                   	State INT NOT NULL,
                   	Type INT NOT NULL,
                   	IsValidYesNo INT NOT NULL,
                   	LastModifiedUTCDateTime TIMESTAMP NULL
                   );
                   
                   INSERT INTO Arrival (
                    Id,
                    IsOnDatedVehicleJourneyId,
                    IsOnMonitoredVehicleJourneyId,
                    JourneyPatternSequenceNumber,
                    IsTimetabledAtJourneyPatternPointGid,
                    VisitCountNumber,
                    IsTargetedAtJourneyPatternPointGid,
                    WasObservedAtJourneyPatternPointGid,
                    TimetabledLatestDateTime,
                    TargetDateTime,
                    EstimatedDateTime,
                    ObservedDateTime,
                    State,
                    Type,
                    IsValidYesNo,
                    LastModifiedUTCDateTime
                  ) VALUES (
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    2,
                    2,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    3,
                    3,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    4,
                    4,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  );
                   
                   INSERT INTO Departure (
                    Id,
                    IsOnDatedVehicleJourneyId,
                    IsOnMonitoredVehicleJourneyId,
                    JourneyPatternSequenceNumber,
                    IsTimetabledAtJourneyPatternPointGid,
                    VisitCountNumber,
                    IsTargetedAtJourneyPatternPointGid,
                    WasObservedAtJourneyPatternPointGid,
                    TimetabledEarliestDateTime,
                    TargetDateTime,
                    EstimatedDateTime,
                    ObservedDateTime,
                    State,
                    Type,
                    IsValidYesNo,
                    LastModifiedUTCDateTime
                  ) VALUES (
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    2,
                    2,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(1).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    3,
                    3,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(2).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  ), (
                    1,
                    1,
                    1,
                    4,
                    4,
                    1,
                    1,
                    NULL,
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    '${firstStopTime.plusMinutes(3).format(DATE_TIME_FORMATTER)}',
                    NULL,
                    10,
                    1,
                    1,
                    '$lastModified'
                  );
                """.trimIndent()
            ).use { it.execute() }
        }
    }
}