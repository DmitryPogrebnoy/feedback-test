package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.show.action

import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.state.services.EditingStatisticsService
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.state.services.FeedbackDatesService
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.state.services.ProjectsStatisticService
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.state.services.TasksStatisticService
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.state.task.converter.TasksStatisticConverter
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.user.LostUserType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware

class StatisticPluginShow : AnAction(), DumbAware {

    private val tasksStatisticService: TasksStatisticService = service()
    private val editingStatisticsService: EditingStatisticsService = service()
    private val feedbackDatesService: FeedbackDatesService = service()
    private val projectsStatisticService: ProjectsStatisticService = service()
    private val tasksStatisticConverter: TasksStatisticConverter = TasksStatisticConverter()
    private val gsonPrettyPrinter: Gson = GsonBuilder().disableHtmlEscaping()
            .enableComplexMapKeySerialization().setPrettyPrinting().create()


    override fun actionPerformed(e: AnActionEvent) {
        /*
        val notification = Notification(
                KotlinFeedbackNotificationGroup.group.displayId,
                "Collected statistic",
                "<html>${getBeginnerCustomQuestion()}<br>" +
                        "Is satisfies BeginnerUserType: ${BeginnerUserType.isUserSatisfiesUserType()}<br>" +
                        "Is satisfies SimpleUserType: ${SimpleUserType.isUserSatisfiesUserType()}<br>" +
                        "Is satisfies ActiveUserType: ${ActiveUserType.isUserSatisfiesUserType()}<br><br>" +
                        "Data sharing consent: ${isSendFusEnabled()}<br>" +
                        "Projects info: ${gsonPrettyPrinter.toJson(
                                projectsStatisticService.state!!.projectsStatisticState)}<br>" +
                        "Last active time: ${LastActive.lastActive}<br>" +
                        "Is Kotlin project: ${isKotlinProject(e.project!!)}<br>" +
                        "Is EAP Intellij IDEA: ${isIntellijIdeaEAP()}<br>" +
                        "Is EAP Kotlin plugin: ${isKotlinPluginEAP()}<br>" +
                        " " +
                        "Tasks info - " +
                        gsonPrettyPrinter.toJson(
                                tasksStatisticService.state!!.projectsTasksInfo
                        ) + "<br>" +
                        "Count of Kotlin file editing - " +
                        "${gsonPrettyPrinter.toJson(
                                editingStatisticsService.state!!.countEditKotlinFile)}<br>" +
                        "Last date for show feedback notification " +
                        "${gsonPrettyPrinter.toJson(
                                feedbackDatesService.state!!.dateShowFeedbackNotification)}<br>" +
                        "Last date for send feedback ${gsonPrettyPrinter.toJson(
                                feedbackDatesService.state!!.dateSendFeedback)}<br>" +
                        "Last date for close or cancel feedback dialog ${gsonPrettyPrinter.toJson(
                                feedbackDatesService.state!!.dateCloseFeedbackDialog)}<html>",
                NotificationType.INFORMATION
        )

        notification.notify(e.project)

         */
        LostUserType.showFeedbackDialog(e.project!!)

    }
}