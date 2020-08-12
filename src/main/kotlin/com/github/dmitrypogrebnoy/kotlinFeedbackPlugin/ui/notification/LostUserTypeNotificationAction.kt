package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification

import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.bundle.FeedbackBundle
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.user.LostUserType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Notification action for lost user type.
 * When executed, it opens a feedback dialog for the lost user type.
 */

class LostUserTypeNotificationAction : NotificationAction(
        FeedbackBundle.message("request.feedback.default.notification.action.submit")
) {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        LostUserType.showFeedbackDialog(e.project ?: return)
    }
}
