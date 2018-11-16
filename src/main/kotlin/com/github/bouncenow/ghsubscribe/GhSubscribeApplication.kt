package com.github.bouncenow.ghsubscribe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GhSubscribeApplication

fun main(args: Array<String>) {
    runApplication<GhSubscribeApplication>(*args)
}
