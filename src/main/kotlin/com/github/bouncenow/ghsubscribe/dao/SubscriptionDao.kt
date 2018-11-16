package com.github.bouncenow.ghsubscribe.dao

import com.github.bouncenow.db.app.tables.Subscriptions.SUBSCRIPTIONS
import com.github.bouncenow.ghsubscribe.model.RepositoryId
import com.github.bouncenow.ghsubscribe.model.Subscription
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.types.DayToSecond
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class SubscriptionDao(
        private val create: DSLContext
) {

    fun create(subscription: Subscription): Subscription {
        val record = create.newRecord(SUBSCRIPTIONS)

        record.email = subscription.email
        record.repositoryName = subscription.repository.name
        record.repositoryOwner = subscription.repository.owner
        record.lastCheckTs = subscription.lastCheck
        record.checkIntervalInSeconds = subscription.checkInterval.seconds
        record.store()

        return subscription
                .copy(id = record.id)
    }

    fun getSubscriptionWithExpiredCheckIntervalForUpdate(): Subscription? {
        return create
                .select(*SUBSCRIPTIONS.fields())
                .from(SUBSCRIPTIONS)
                .where(
                        SUBSCRIPTIONS.LAST_CHECK_TS
                                .plus(SUBSCRIPTIONS.CHECK_INTERVAL_IN_SECONDS
                                        .times(field("interval '1 sec'", DayToSecond::class.java)))
                                .lessOrEqual(LocalDateTime.now())
                )
                .orderBy(SUBSCRIPTIONS.ID)
                .limit(1)
                .forUpdate().skipLocked()
                .fetchAny { r ->
                    Subscription(
                            id = r[SUBSCRIPTIONS.ID],
                            email = r[SUBSCRIPTIONS.EMAIL],
                            repository = RepositoryId(
                                    name = r[SUBSCRIPTIONS.REPOSITORY_NAME],
                                    owner = r[SUBSCRIPTIONS.REPOSITORY_OWNER]
                            ),
                            checkInterval = Duration.ofSeconds(r[SUBSCRIPTIONS.CHECK_INTERVAL_IN_SECONDS]),
                            lastCheck = r[SUBSCRIPTIONS.LAST_CHECK_TS]
                    )
                }
    }

    fun updateSubscriptionCheckTime(id: Long, newCheckTime: LocalDateTime) {
        create
                .update(SUBSCRIPTIONS)
                .set(SUBSCRIPTIONS.LAST_CHECK_TS, newCheckTime)
                .where(SUBSCRIPTIONS.ID.eq(id))
                .execute()
    }

}