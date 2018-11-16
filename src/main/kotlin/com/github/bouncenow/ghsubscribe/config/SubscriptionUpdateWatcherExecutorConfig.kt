package com.github.bouncenow.ghsubscribe.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@ConfigurationProperties("update-watcher")
class SubscriptionUpdateWatcherExecutorConfig(
        private val subscriptionUpdateWatcherConfig: SubscriptionUpdateWatcherConfig
) {

    @Bean()
    fun updateWatcherExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = subscriptionUpdateWatcherConfig.corePoolSize!!
        executor.maxPoolSize = subscriptionUpdateWatcherConfig.maxPoolSize!!
        executor.initialize()

        return executor
    }

}

