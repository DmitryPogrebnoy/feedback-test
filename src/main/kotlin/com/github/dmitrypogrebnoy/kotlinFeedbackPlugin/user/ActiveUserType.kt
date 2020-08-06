package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.user

import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.dialog.ActiveUserFeedbackDialog
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification.ActiveUserTypeNotificationAction
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification.RequestFeedbackNotification
import com.intellij.openapi.project.Project

object ActiveUserType : UserType {

    override val userTypeName: String = "Active Kotlin user"

    override fun isUserSatisfiesUserType(): Boolean {
        return needCollectUserFeedback() && isKotlinPluginEAP() && isKotlinPluginEnabled()
                && checkRelevantNumberKotlinFileEditing()
    }

    override fun showFeedbackNotification(project: Project) {
        val notification = RequestFeedbackNotification()
        notification.addAction(ActiveUserTypeNotificationAction())
        notification.notify(project)
    }

    override fun showFeedbackDialog(project: Project) {
        ActiveUserFeedbackDialog(project).show()
    }
}