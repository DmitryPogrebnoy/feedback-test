package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup

object KotlinFeedbackNotificationGroup {
    val group = NotificationGroup(
            "Kotlin Feedback Notifications",
            NotificationDisplayType.BALLOON,
            true
    )
}