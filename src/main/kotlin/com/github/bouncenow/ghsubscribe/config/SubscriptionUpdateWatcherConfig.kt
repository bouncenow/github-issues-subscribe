package com.github.bouncenow.ghsubscribe.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("update-watcher")
class SubscriptionUpdateWatcherConfig {

    var maxTaskCountToRun: Int? = null

    var corePoolSize: Int? = null

    var maxPoolSize: Int? = null

}