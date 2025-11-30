package com.khan366kos.common.model

import kotlinx.serialization.Serializable

/**
 * Represents the lifecycle status of a task
 */
@Serializable
enum class TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE,
    CANCELED
}