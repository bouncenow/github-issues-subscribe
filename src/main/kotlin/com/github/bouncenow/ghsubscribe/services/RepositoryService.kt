package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.model.GithubRepository
import com.github.bouncenow.ghsubscribe.model.RepositoryId
import com.github.bouncenow.githubapi.GetRepositoryByNameAndOwnerQuery
import com.github.bouncenow.githubapi.GetTopRepositoriesQuery
import org.springframework.stereotype.Service

@Service
class RepositoryService(
        private val githubApiService: GithubApiService
) {

    fun getTopRepositories(language: String, count: Int): List<GithubRepository> {
        val data = githubApiService.queryAndGetSynchronously(
                GetTopRepositoriesQuery.builder()
                        .query("language:$language sort:stars-desc")
                        .count(count)
                        .build()
        )

        return data
                .search()
                .edges()
                ?.mapNotNull {
                    it.node() as? GetTopRepositoriesQuery.AsRepository
                }
                ?.map {
                    GithubRepository(
                            name = it.name(),
                            description = it.description(),
                            owner = it.owner().login(),
                            stars = it.stargazers().totalCount(),
                            forks = it.forks().totalCount()
                    )
                }
                ?: emptyList()
    }

    fun checkRepositoryExistence(repository: RepositoryId): Boolean {
        val data = githubApiService.queryAndGetSynchronously(
                GetRepositoryByNameAndOwnerQuery
                        .builder()
                        .name(repository.name)
                        .owner(repository.owner)
                        .build()
        )

        return data.repository() != null
    }

}