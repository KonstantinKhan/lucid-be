package com.khan366kos.common.model

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TaskTest {

    private val sampleTask = Task(
        id = "task-123",
        title = "Sample Task",
        description = "A test task",
        createdAt = Instant.parse("2023-11-30T10:00:00Z"),
        updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
        status = TaskStatus.NEW,
        authorId = "user-456",
        assigneeIds = listOf("user-789"),
        priority = 1,
        plannedTime = 3600,
        actualTime = null
    )

    @Test
    fun `task creation with valid data succeeds`() {
        val task = Task(
            id = "test-1",
            title = "Test",
            createdAt = Instant.parse("2023-11-30T10:00:00Z"),
            updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
            status = TaskStatus.NEW,
            authorId = "author-1"
        )

        assertEquals("test-1", task.id)
        assertEquals("Test", task.title)
        assertEquals(TaskStatus.NEW, task.status)
        assertTrue(task.assigneeIds.isEmpty())
    }

    @Test
    fun `task with blank title fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "   ",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1"
            )
        }
    }

    @Test
    fun `task with empty title fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1"
            )
        }
    }

    @Test
    fun `task with negative priority fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "Test",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1",
                priority = -1
            )
        }
    }

    @Test
    fun `task with zero priority fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "Test",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1",
                priority = 0
            )
        }
    }

    @Test
    fun `task with negative planned time fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "Test",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1",
                plannedTime = -1
            )
        }
    }

    @Test
    fun `task with negative actual time fails validation`() {
        assertFailsWith<IllegalArgumentException> {
            Task(
                id = "test-1",
                title = "Test",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = TaskStatus.NEW,
                authorId = "author-1",
                actualTime = -100
            )
        }
    }

    @Test
    fun `task serialization and deserialization works correctly`() {
        val json = Json.encodeToString(sampleTask)
        val deserialized = Json.decodeFromString<Task>(json)

        assertEquals(sampleTask, deserialized)
    }

    @Test
    fun `task copy creates new instance with updated fields`() {
        val updated = sampleTask.copy(
            status = TaskStatus.IN_PROGRESS,
            updatedAt = Instant.parse("2023-11-30T11:00:00Z")
        )

        assertEquals(TaskStatus.IN_PROGRESS, updated.status)
        assertEquals(sampleTask.id, updated.id)
        assertEquals(sampleTask.title, updated.title)
    }

    @Test
    fun `task with all optional fields as null succeeds`() {
        val task = Task(
            id = "test-1",
            title = "Minimal Task",
            createdAt = Instant.parse("2023-11-30T10:00:00Z"),
            updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
            status = TaskStatus.NEW,
            authorId = "author-1",
            description = null,
            priority = null,
            plannedTime = null,
            actualTime = null
        )

        assertEquals("Minimal Task", task.title)
        assertEquals(null, task.description)
        assertEquals(null, task.priority)
        assertTrue(task.assigneeIds.isEmpty())
    }

    @Test
    fun `task with all status values succeeds`() {
        val statuses = listOf(
            TaskStatus.NEW,
            TaskStatus.IN_PROGRESS,
            TaskStatus.DONE,
            TaskStatus.CANCELED
        )

        statuses.forEach { status ->
            val task = Task(
                id = "test-${status.name}",
                title = "Test",
                createdAt = Instant.parse("2023-11-30T10:00:00Z"),
                updatedAt = Instant.parse("2023-11-30T10:00:00Z"),
                status = status,
                authorId = "author-1"
            )
            assertEquals(status, task.status)
        }
    }
}