package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.config.SubscriptionUpdateWatcherConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Executor

@Service
@EnableScheduling
class SubscriptionUpdateWatcherService(
        private val subscriptionUpdateService: SubscriptionUpdateService,
        @Qualifier("updateWatcherExecutor") private val executor: Executor,
        private val subscriptionUpdateWatcherConfig: SubscriptionUpdateWatcherConfig
) {

    @Scheduled(fixedRate = CHECK_UPDATE_RATE, initialDelay = 0L)
    fun checkForSubscriptionsToCheckForUpdates() {
        repeat(subscriptionUpdateWatcherConfig.maxTaskCountToRun!!) {
            executor.execute {
                subscriptionUpdateService.processSubscriptionForUpdatesIfAny()
            }
        }
    }

    companion object {
        private const val CHECK_UPDATE_RATE = 30000L
    }

}