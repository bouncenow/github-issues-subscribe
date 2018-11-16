package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.model.Issue
import com.github.bouncenow.ghsubscribe.model.RepositoryId
import com.github.bouncenow.githubapi.GetIssuesQuery
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

@Service
@ConfigurationProperties("issues")
class IssueService(
        private val githubApiService: GithubApiService
) {

    var firstCount: Int? = null
    var maxCount: Int? = null

    fun getIssuesForRepositorySinceTime(
            repository: RepositoryId,
            since: LocalDateTime
    ): List<Issue> {
        val firstPage = fetchIssues(repository, since, firstCount!!, null)

        val totalCount = firstPage
                .search()
                .issueCount()

        val edges = ArrayList<GetIssuesQuery.Edge>(totalCount)
        edges.addAll(firstPage
                .search()
                .edges() ?: emptyList()
        )

        if (totalCount > firstCount!!) {
            edges.addAll(fetchRestEdges(repository, since, totalCount, firstPage))
        }

        return edges
                .mapNotNull {
                    it.node() as? GetIssuesQuery.AsIssue
                }
                .map {
                    Issue(
                            title = it.title(),
                            bodyText = it.bodyText(),
                            createdAt = it.createdAt().toString()
                    )
                }

    }

    private fun fetchRestEdges(
            repository: RepositoryId,
            since: LocalDateTime,
            totalCount: Int,
            firstPage: GetIssuesQuery.Data
    ): ArrayList<GetIssuesQuery.Edge> {
        var left = totalCount - firstCount!!
        val edges = ArrayList<GetIssuesQuery.Edge>(left)

        var currentCursor = firstPage
                .search()
                .pageInfo()
                .endCursor()
        while (currentCursor != null) {
            val count = min(maxCount!!, left)
            val nextPage = fetchIssues(repository, since, count, currentCursor)
            edges.addAll(nextPage
                    .search()
                    .edges() ?: emptyList()
            )
            left -= count
            currentCursor = nextPage
                    .search()
                    .pageInfo()
                    .endCursor()
        }
        return edges
    }

    private fun fetchIssues(repository: RepositoryId, since: LocalDateTime, count: Int, after: String?): GetIssuesQuery.Data {
        return githubApiService.queryAndGetSynchronously(
                GetIssuesQuery
                        .builder()
                        .query(getQueryString(repository, since))
                        .count(count)
                        .after(after)
                        .build()
        )
    }

    private fun getQueryString(repository: RepositoryId, since: LocalDateTime) =
            "repo:${repository.owner}/${repository.name} type:issue created:>${since.formatIsoForApi()} sort:created-desc"

    private fun LocalDateTime.formatIsoForApi() = this.withNano(0).format(DateTimeFormatter.ISO_DATE_TIME)
}