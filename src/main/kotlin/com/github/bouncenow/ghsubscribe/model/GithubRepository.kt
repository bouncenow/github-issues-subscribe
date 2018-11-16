package com.github.bouncenow.ghsubscribe.model

data class GithubRepository(
        val name: String,
        val description: String?,
        val owner: String,
        val stars: Int,
        val forks: Int
)