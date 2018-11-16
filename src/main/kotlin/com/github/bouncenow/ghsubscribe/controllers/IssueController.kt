package com.github.bouncenow.ghsubscribe.controllers

import com.github.bouncenow.ghsubscribe.model.RepositoryId
import com.github.bouncenow.ghsubscribe.services.IssueService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class IssueController(
        private val issueService: IssueService
) {

    @GetMapping("/issues")
    fun getIssues(repositoryId: RepositoryId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) since: LocalDateTime) = issueService.getIssuesForRepositorySinceTime(repositoryId, since)

}