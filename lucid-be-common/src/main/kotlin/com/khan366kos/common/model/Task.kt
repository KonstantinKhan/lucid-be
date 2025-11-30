package com.khan366kos.common.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

/**
 * Domain model representing a task in the system.
 *
 * This is the core business entity, separate from API transport models.
 */
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: TaskStatus,
    val authorId: String,
    val assigneeIds: List<String> = emptyList(),
    val priority: Int? = null,
    val plannedTime: Int? = null,
    val actualTime: Int? = null
) {
    init {
        require(title.isNotBlank()) { "Task title cannot be blank" }
        require(priority == null || priority > 0) { "Priority must be positive" }
        require(plannedTime == null || plannedTime >= 0) { "Planned time cannot be negative" }
        require(actualTime == null || actualTime >= 0) { "Actual time cannot be negative" }
    }
}