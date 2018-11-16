package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.model.Issue
import com.github.bouncenow.ghsubscribe.model.Subscription
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SubscriptionUpdateService(
        private val subscriptionService: SubscriptionService,
        private val issueService: IssueService,
        private val emailUpdatesSendService: EmailUpdatesSendService
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processSubscriptionForUpdatesIfAny() {
        val subscriptionToCheckForUpdates = subscriptionService.getSubscriptionsWithExpiredCheckIntervalForUpdate()
                ?: return

        val possibleUpdateTime = LocalDateTime.now()
        val checkStatus = checkForSubscriptionNewIssues(subscriptionToCheckForUpdates)
                ?.let { newIssues ->
                    if (newIssues.isNotEmpty()) {
                        sendIssuesForSubscription(newIssues, subscriptionToCheckForUpdates)
                    } else {
                        true
                    }
                }
                ?: false
        if (checkStatus) {
            subscriptionService.updateSubscriptionCheckTime(subscriptionToCheckForUpdates, possibleUpdateTime)
        }
    }

    private fun checkForSubscriptionNewIssues(subscription: Subscription): List<Issue>? {
        return try {
            issueService.getIssuesForRepositorySinceTime(subscription.repository, subscription.lastCheck)
        } catch (ex: ApiAccessException) {
            logger.error("Error during checking for issues for $subscription:\n${ex.message}")
            null
        }
    }

    private fun sendIssuesForSubscription(issues: List<Issue>, subscription: Subscription): Boolean {
        return try {
            emailUpdatesSendService.sendIssuesForSubscription(subscription, issues)
            true
        } catch (ex: MailException) {
            logger.error("Error during sending issues for $subscription: ${ex.message}")
            false
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SubscriptionUpdateWatcherService::class.java)
    }

}