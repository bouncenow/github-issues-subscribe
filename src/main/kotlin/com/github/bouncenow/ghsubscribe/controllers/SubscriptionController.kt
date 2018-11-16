package com.github.bouncenow.ghsubscribe.controllers

import com.github.bouncenow.ghsubscribe.model.SubscriptionRequest
import com.github.bouncenow.ghsubscribe.services.SubscriptionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/subscriptions")
class SubscriptionController(
        private val subscriptionService: SubscriptionService
) {

    @PostMapping
    fun create(@RequestBody request: SubscriptionRequest) = subscriptionService.createSubscription(request)
}