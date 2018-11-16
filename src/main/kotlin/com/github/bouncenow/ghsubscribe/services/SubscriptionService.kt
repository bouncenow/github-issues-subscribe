package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.dao.SubscriptionDao
import com.github.bouncenow.ghsubscribe.model.Subscription
import com.github.bouncenow.ghsubscribe.model.SubscriptionRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime

@Service
class SubscriptionService(
        private val subscriptionDao: SubscriptionDao,
        private val repositoryService: RepositoryService
) {

    fun createSubscription(request: SubscriptionRequest): Subscription {
        val repositoryExists = repositoryService.checkRepositoryExistence(request.repository)
        if (!repositoryExists) {
            throw RepositoryNotExistsException("Repository ${request.repository} doesn't exist")
        }

        return subscriptionDao.create(
                Subscription(
                        id = null,
                        email = request.email,
                        repository = request.repository,
                        checkInterval = request.checkInterval,
                        lastCheck = LocalDateTime.now()
                )
        )
    }

    @Transactional
    fun getSubscriptionsWithExpiredCheckIntervalForUpdate() = subscriptionDao.getSubscriptionWithExpiredCheckIntervalForUpdate()

    fun updateSubscriptionCheckTime(subscription: Subscription, newCheckTime: LocalDateTime) {
        subscription.id
                ?: throw IllegalArgumentException("Subscription $subscription to update should have an id")
        subscriptionDao.updateSubscriptionCheckTime(subscription.id, newCheckTime)
    }

}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RepositoryNotExistsException(message: String) : RuntimeException(message)