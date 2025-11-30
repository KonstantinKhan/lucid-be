package com.khan366kos.mappers

import com.khan366kos.common.model.Task as DomainTask
import com.khan366kos.common.model.TaskStatus as DomainTaskStatus
import com.khan366kos.transport.model.Task as TransportTask
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TaskMappersTest : ShouldSpec({
    context("DomainTask mapping") {
        should("map all fields correctly to TransportTask") {
            val domainTask = DomainTask(
                id = "task-123",
                title = "Test Task",
                description = "A sample task",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T11:00:00Z"),
                status = DomainTaskStatus.IN_PROGRESS,
                authorId = "author-456",
                assigneeIds = listOf("user-789", "user-101"),
                priority = 2,
                plannedTime = 3600,
                actualTime = 1800
            )

            val transportTask = domainTask.toTransport()

            transportTask.id shouldBe "task-123"
            transportTask.title shouldBe "Test Task"
            transportTask.description shouldBe "A sample task"
            transportTask.createdAt shouldBe OffsetDateTime.of(2023, 11, 30, 10, 0, 0, 0, ZoneOffset.UTC)
            transportTask.updatedAt shouldBe OffsetDateTime.of(2023, 11, 30, 11, 0, 0, 0, ZoneOffset.UTC)
            transportTask.status shouldBe TransportTask.Status.in_progress
            transportTask.authorId shouldBe "author-456"
            transportTask.assigneeIds shouldBe listOf("user-789", "user-101")
            transportTask.priority shouldBe 2
            transportTask.plannedTime shouldBe 3600
            transportTask.actualTime shouldBe 1800
        }

        should("handle empty assigneeIds correctly when mapping to TransportTask") {
            val domainTaskWithEmptyAssignees = DomainTask(
                id = "task-123",
                title = "Test Task",
                description = "A sample task",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T11:00:00Z"),
                status = DomainTaskStatus.NEW,
                authorId = "author-456",
                assigneeIds = emptyList(),
                priority = 2,
                plannedTime = 3600,
                actualTime = 1800
            )

            val transportTask = domainTaskWithEmptyAssignees.toTransport()

            transportTask.assigneeIds shouldBe null
        }
    }

    context("TransportTask mapping") {
        should("map all fields correctly to DomainTask") {
            val transportTask = TransportTask(
                id = "task-123",
                title = "Test Task",
                description = "A sample task",
                createdAt = OffsetDateTime.of(2023, 11, 30, 10, 0, 0, 0, ZoneOffset.UTC),
                updatedAt = OffsetDateTime.of(2023, 11, 30, 11, 0, 0, 0, ZoneOffset.UTC),
                status = TransportTask.Status.done,
                authorId = "author-456",
                assigneeIds = listOf("user-789", "user-101"),
                priority = 2,
                plannedTime = 3600,
                actualTime = 1800
            )

            val domainTask = transportTask.toDomain()

            domainTask.id shouldBe "task-123"
            domainTask.title shouldBe "Test Task"
            domainTask.description shouldBe "A sample task"
            domainTask.createdAt shouldBe Instant.parse("2023-11-30T10:00:00Z")
            domainTask.updatedAt shouldBe Instant.parse("2023-11-30T11:00:00Z")
            domainTask.status shouldBe DomainTaskStatus.DONE
            domainTask.authorId shouldBe "author-456"
            domainTask.assigneeIds shouldBe listOf("user-789", "user-101")
            domainTask.priority shouldBe 2
            domainTask.plannedTime shouldBe 3600
            domainTask.actualTime shouldBe 1800
        }

        should("handle null assigneeIds correctly when mapping to DomainTask") {
            val transportTask = TransportTask(
                id = "task-123",
                title = "Test Task",
                description = "A sample task",
                createdAt = OffsetDateTime.of(2023, 11, 30, 10, 0, 0, 0, ZoneOffset.UTC),
                updatedAt = OffsetDateTime.of(2023, 11, 30, 11, 0, 0, 0, ZoneOffset.UTC),
                status = TransportTask.Status.new,
                authorId = "author-456",
                assigneeIds = null,
                priority = 2,
                plannedTime = 3600,
                actualTime = 1800
            )

            val domainTask = transportTask.toDomain()

            domainTask.assigneeIds shouldBe emptyList()
        }
    }

    context("TaskStatus mapping") {
        should("map all DomainTaskStatus values correctly to TransportTask.Status") {
            DomainTaskStatus.NEW.toTransport() shouldBe TransportTask.Status.new
            DomainTaskStatus.IN_PROGRESS.toTransport() shouldBe TransportTask.Status.in_progress
            DomainTaskStatus.DONE.toTransport() shouldBe TransportTask.Status.done
            DomainTaskStatus.CANCELED.toTransport() shouldBe TransportTask.Status.canceled
        }

        should("map all TransportTask.Status values correctly to DomainTaskStatus") {
            TransportTask.Status.new.toDomain() shouldBe DomainTaskStatus.NEW
            TransportTask.Status.in_progress.toDomain() shouldBe DomainTaskStatus.IN_PROGRESS
            TransportTask.Status.done.toDomain() shouldBe DomainTaskStatus.DONE
            TransportTask.Status.canceled.toDomain() shouldBe DomainTaskStatus.CANCELED
        }
    }

    context("Round-trip mapping") {
        should("preserve data when mapping DomainTask -> TransportTask -> DomainTask") {
            val originalDomainTask = DomainTask(
                id = "task-123",
                title = "Test Task",
                description = "A sample task",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T11:00:00Z"),
                status = DomainTaskStatus.IN_PROGRESS,
                authorId = "author-456",
                assigneeIds = listOf("user-789", "user-101"),
                priority = 2,
                plannedTime = 3600,
                actualTime = 1800
            )

            val transportTask = originalDomainTask.toTransport()
            val roundTripDomainTask = transportTask.toDomain()

            roundTripDomainTask.id shouldBe originalDomainTask.id
            roundTripDomainTask.title shouldBe originalDomainTask.title
            roundTripDomainTask.description shouldBe originalDomainTask.description
            roundTripDomainTask.createdAt shouldBe originalDomainTask.createdAt
            roundTripDomainTask.updatedAt shouldBe originalDomainTask.updatedAt
            roundTripDomainTask.status shouldBe originalDomainTask.status
            roundTripDomainTask.authorId shouldBe originalDomainTask.authorId
            roundTripDomainTask.assigneeIds shouldBe originalDomainTask.assigneeIds
            roundTripDomainTask.priority shouldBe originalDomainTask.priority
            roundTripDomainTask.plannedTime shouldBe originalDomainTask.plannedTime
            roundTripDomainTask.actualTime shouldBe originalDomainTask.actualTime
        }
    }

    context("Time conversion") {
        should("convert Instant to OffsetDateTime correctly") {
            val instant = Instant.parse("2023-11-30T10:30:45Z")
            val offsetDateTime = instant.toOffsetDateTime()

            offsetDateTime shouldBe OffsetDateTime.of(2023, 11, 30, 10, 30, 45, 0, ZoneOffset.UTC)
        }

        should("convert OffsetDateTime to Instant correctly") {
            val offsetDateTime = OffsetDateTime.of(2023, 11, 30, 10, 30, 45, 0, ZoneOffset.UTC)
            val instant = offsetDateTime.toKotlinInstant()

            instant shouldBe Instant.parse("2023-11-30T10:30:45Z")
        }
    }
})