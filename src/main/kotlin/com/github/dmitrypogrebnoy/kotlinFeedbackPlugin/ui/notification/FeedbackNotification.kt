package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification

import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.bundle.FeedbackBundle.message
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.dialog.FeedbackDialog
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class FeedbackNotification(private val project: Project) {

    private val feedbackNotification = Notification(
            "Feedback Notification",
            message("notification.title"),
            message("notification.content"),
            NotificationType.INFORMATION
    ).addAction(
            NotificationAction.createSimple(message("notification.action.submit")) {
                val dialog = FeedbackDialog(project)
                dialog.show()
            }
    )

    fun notificationNotify() {
        feedbackNotification.notify(project)
    }
}