package kotlinFeedbackPlugin.track.task.build.time

import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import kotlinFeedbackPlugin.show.showGradleExecuteTaskTimeNotificationIfPossible
import kotlinFeedbackPlugin.state.task.GRADLE_TASKS_FOR_TRACK
import kotlinFeedbackPlugin.state.task.GradleProjectTask
import kotlinFeedbackPlugin.state.task.GradleTaskTempInfo
import kotlinFeedbackPlugin.state.task.TempTaskInfo
import org.jetbrains.plugins.gradle.service.task.GradleTaskManagerExtension
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings
import java.time.LocalTime

/**
 * Tracks the start of the EXECUTE_TYPE of Gradle task type.
 *
 * @see kotlinFeedbackPlugin.track.task.build.time.GradleTaskListener
 */
class GradleTaskManager : GradleTaskManagerExtension {

    //Only executed if gradle task has EXECUTE_TYPE type
    override fun executeTasks(id: ExternalSystemTaskId,
                              taskNames: MutableList<String>,
                              projectPath: String,
                              settings: GradleExecutionSettings?,
                              jvmParametersSetup: String?,
                              listener: ExternalSystemTaskNotificationListener): Boolean {

        if (taskNames.size == 1 && GRADLE_TASKS_FOR_TRACK.contains(taskNames[0])) {
            val project = id.findProject() ?: return false

            showGradleExecuteTaskTimeNotificationIfPossible(project, taskNames[0])

            TempTaskInfo.gradleExecuteTasksStartInfo[
                    GradleProjectTask(
                            project.name,
                            id.toString()
                    )
            ] = GradleTaskTempInfo(LocalTime.now().toSecondOfDay(), taskNames[0])
        }

        //should return false, else not show output and strange behavior
        return false
    }

    override fun cancelTask(id: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener): Boolean {
        //should return true, else error
        return true
    }
}