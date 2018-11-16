package com.github.bouncenow.ghsubscribe.model

import java.time.Duration
import java.time.LocalDateTime

data class Subscription(
        val id: Long?,
        val email: String,
        val repository: RepositoryId,
        val checkInterval: Duration,
        val lastCheck: LocalDateTime
)

data class RepositoryId(
        val name: String,
        val owner: String
)