package com.github.bouncenow.ghsubscribe.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("email")
class EmailConfig {

    lateinit var from: String

}