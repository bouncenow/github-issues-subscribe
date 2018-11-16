package com.github.bouncenow.ghsubscribe.model

import java.time.Duration

data class SubscriptionRequest(
        val email: String,
        val repository: RepositoryId,
        val checkInterval: Duration
)