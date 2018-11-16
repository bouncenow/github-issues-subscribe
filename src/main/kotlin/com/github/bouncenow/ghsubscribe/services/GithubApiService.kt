package com.github.bouncenow.ghsubscribe.services

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.rx.RxApollo
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus

@Service
class GithubApiService(
        private val githubClient: ApolloClient
) {

    fun <T, D : Operation.Data?, V : Operation.Variables?> queryAndGetSynchronously(query: Query<D, T, V>): T {
        val call = githubClient.query(query)
        val response = RxApollo.from(call).toBlocking().single()
        val data = response.data()
        return if (data != null) {
            data
        } else {
            logger.error("Errors during api call: ${query.name()}")
            for (e in response.errors()) {
                logger.error(e.message())
            }
            throw ApiAccessException("Error during api call")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GithubApiService::class.java)
    }

}

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class ApiAccessException(message: String): RuntimeException(message)