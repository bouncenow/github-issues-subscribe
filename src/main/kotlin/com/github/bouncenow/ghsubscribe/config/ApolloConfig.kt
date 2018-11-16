package com.github.bouncenow.ghsubscribe.config

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import com.github.bouncenow.githubapi.type.CustomType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
@ConfigurationProperties("github-api")
class ApolloConfig {

    lateinit var url: String
    lateinit var token: String

    @Bean
    fun githubClient(): ApolloClient {

        val client = OkHttpClient.Builder()
                .addInterceptor(this::addingAuthorizationHeaderInterceptor)
                .build()

        val customDateTimeAdapter = customDateTimeAdapter()

        return ApolloClient
                .builder()
                .serverUrl(url)
                .okHttpClient(client)
                .addCustomTypeAdapter(CustomType.DATETIME, customDateTimeAdapter)
                .build()
    }

    private fun addingAuthorizationHeaderInterceptor(chain: Interceptor.Chain): Response? {
        val original = chain.request()
        val builder = original.newBuilder().method(original.method(), original.body())
        builder.addHeader(AUTHORIZATION_HEADER, "Bearer $token")
        return chain.proceed(builder.build())
    }

    private fun customDateTimeAdapter() =
            object: CustomTypeAdapter<LocalDateTime> {
                override fun encode(value: LocalDateTime): CustomTypeValue<*> {
                    return CustomTypeValue.GraphQLString(DateTimeFormatter.ISO_DATE_TIME.format(value))
                }

                override fun decode(value: CustomTypeValue<*>): LocalDateTime {
                    return DateTimeFormatter.ISO_DATE_TIME.parse(value.value.toString(), LocalDateTime::from)
                }

            }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}