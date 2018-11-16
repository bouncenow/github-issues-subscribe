package com.github.bouncenow.ghsubscribe.services

import com.github.bouncenow.ghsubscribe.config.EmailConfig
import com.github.bouncenow.ghsubscribe.model.Issue
import com.github.bouncenow.ghsubscribe.model.Subscription
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class EmailUpdatesSendService(
        private val emailConfig: EmailConfig,
        private val mailSender: MailSender
) {

    fun sendIssuesForSubscription(subscription: Subscription, issues: List<Issue>) {
        val message = SimpleMailMessage()

        message.setFrom(emailConfig.from)
        message.setTo(subscription.email)
        message.setSubject(createSubject(subscription))
        message.setText(createEmailTextForIssues(issues))

        mailSender.send(message)
    }

    private fun createSubject(subscription: Subscription): String {
        return "New issues from ${subscription.repository.owner}/${subscription.repository.name} since ${subscription.lastCheck.format(DATE_TIME_FORMAT)}"
    }

    private fun createEmailTextForIssues(issues: List<Issue>): String {

        return issues.joinToString(separator = "\n\n") {
            "Issue: ${it.title}\ncreated at: ${it.createdAt}\ntext: ${it.bodyText}"
        }

    }

    companion object {
        private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
    }


}