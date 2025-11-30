package com.khan366kos.mappers

import com.khan366kos.common.model.Task as DomainTask
import com.khan366kos.common.model.TaskStatus as DomainTaskStatus
import com.khan366kos.transport.model.Task as TransportTask
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Maps between domain models (kotlinx.serialization) and transport models (Jackson)
 */

// Domain -> Transport

fun DomainTask.toTransport(): TransportTask = TransportTask(
    id = this.id,
    title = this.title,
    description = this.description,
    createdAt = this.createdAt.toOffsetDateTime(),
    updatedAt = this.updatedAt.toOffsetDateTime(),
    status = this.status.toTransport(),
    authorId = this.authorId,
    assigneeIds = this.assigneeIds.takeIf { it.isNotEmpty() },
    priority = this.priority,
    plannedTime = this.plannedTime,
    actualTime = this.actualTime
)

fun DomainTaskStatus.toTransport(): TransportTask.Status = when (this) {
    DomainTaskStatus.NEW -> TransportTask.Status.new
    DomainTaskStatus.IN_PROGRESS -> TransportTask.Status.in_progress
    DomainTaskStatus.DONE -> TransportTask.Status.done
    DomainTaskStatus.CANCELED -> TransportTask.Status.canceled
}

// Transport -> Domain

fun TransportTask.toDomain(): DomainTask = DomainTask(
    id = this.id,
    title = this.title,
    description = this.description,
    createdAt = this.createdAt.toKotlinInstant(),
    updatedAt = this.updatedAt.toKotlinInstant(),
    status = this.status.toDomain(),
    authorId = this.authorId,
    assigneeIds = this.assigneeIds ?: emptyList(),
    priority = this.priority,
    plannedTime = this.plannedTime,
    actualTime = this.actualTime
)

fun TransportTask.Status.toDomain(): DomainTaskStatus = when (this) {
    TransportTask.Status.new -> DomainTaskStatus.NEW
    TransportTask.Status.in_progress -> DomainTaskStatus.IN_PROGRESS
    TransportTask.Status.done -> DomainTaskStatus.DONE
    TransportTask.Status.canceled -> DomainTaskStatus.CANCELED
}

// Time conversion helpers

private fun Instant.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(this.toJavaInstant(), ZoneOffset.UTC)

private fun OffsetDateTime.toKotlinInstant(): Instant {
    val javaInstant: java.time.Instant = this.toInstant()
    return javaInstant.toKotlinInstant()
}