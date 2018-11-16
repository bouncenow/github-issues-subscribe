package com.github.bouncenow.ghsubscribe.controllers

import com.github.bouncenow.ghsubscribe.services.RepositoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repositories")
class RepositoryController(
        private val repositoryService: RepositoryService
) {

    @GetMapping
    fun topRepositories(
            @RequestParam language: String,
            @RequestParam(defaultValue = 10.toString()) count: Int
    ) = repositoryService.getTopRepositories(language, count)

}