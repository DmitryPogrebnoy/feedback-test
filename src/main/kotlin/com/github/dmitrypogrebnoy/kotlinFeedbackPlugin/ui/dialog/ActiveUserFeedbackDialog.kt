package com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.dialog

import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.bundle.FeedbackBundle
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.network.FeedbackSender
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.ui.notification.SuccessSendFeedbackNotification
import com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.user.ActiveUserType
import com.intellij.ide.ui.laf.darcula.ui.DarculaPanelUI
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.layout.panel
import java.awt.event.ActionEvent
import java.io.File
import java.io.IOException
import java.time.LocalDate
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.UIManager

/**
 * Feedback dialog for active user type.
 *
 * @see com.github.dmitrypogrebnoy.kotlinFeedbackPlugin.user.ActiveUserType
 */

class ActiveUserFeedbackDialog(project: Project) : AbstractFeedbackDialog(project) {

    override val titleLabel: JBLabel
    override val sectionLabel: JBLabel
    override val feedbackLabel: JBLabel
    override val feedbackTextArea: EditorTextField
    override val feedbackDialogPanel: DialogPanel
    override val customQuestionLabel: JBLabel?
    override val customQuestionTextArea: EditorTextField?
    private val attachFileLabel: JBLabel
    private val attachFile: TextFieldWithBrowseButton
    override val successSendFeedbackNotification: SuccessSendFeedbackNotification

    init {
        setFieldsDialog()
        //TODO: Remove " ACTIVE "
        titleLabel = createTitleLabel(FeedbackBundle.message("dialog.default.content.title") + " ACTIVE ")
        sectionLabel = createSectionLabel(FeedbackBundle.message("dialog.default.content.section"))
        feedbackLabel = createFeedbackLabel(FeedbackBundle.message("dialog.default.content.description.label"))
        feedbackTextArea = createFeedbackTextArea(
                project,
                FeedbackBundle.message("dialog.default.content.description.textarea.placeholder")
        )
        feedbackLabel.labelFor = feedbackTextArea
        attachFileLabel = createAttachFileLabel(FeedbackBundle.message("dialog.default.content.attach.file.label"))
        attachFile = createAttachFileChooser(
                project,
                FeedbackBundle.message("dialog.default.content.attach.file.title"),
                FeedbackBundle.message("dialog.default.content.attach.file.description")
        )
        customQuestionLabel = createCustomQuestionLabel(ActiveUserType.customQuestion)
        customQuestionTextArea = createCustomQuestionTextField(ActiveUserType.customQuestion)

        feedbackDialogPanel = createFeedbackDialogPanel()

        successSendFeedbackNotification = SuccessSendFeedbackNotification()

        super.init()
        startTrackingValidation()
    }

    override fun setFieldsDialog() {
        title = FeedbackBundle.message("dialog.default.title")
        myOKAction = sendFeedbackAction()
        setOKButtonText(FeedbackBundle.message("dialog.default.button.ok"))
    }

    override fun sendFeedbackAction(): Action {
        return object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                //first close window
                if (doValidateAll().isEmpty()) {
                    if (feedbackDatesService.state != null) {
                        feedbackDatesService.state!!.dateSendFeedback = LocalDate.now()
                    }
                    close(OK_EXIT_CODE)
                    successSendFeedbackNotification.notify(project)

                    val textBody = StringBuilder()
                    textBody.append("Feedback ")
                    textBody.append("\n\n")
                    textBody.append(feedbackTextArea.text)
                    textBody.append("\n\n\n")
                    if (customQuestionLabel != null) {
                        textBody.append(customQuestionLabel.text)
                    }
                    if (customQuestionTextArea != null) {
                        textBody.append("\n\n" + customQuestionTextArea.text)
                    }

                    //then try to send feedback
                    try {
                        val newIssueId = FeedbackSender.createFeedbackIssue(
                                ActiveUserType.userTypeName + "feedback",
                                textBody.toString()
                        )
                        if (attachFile.text.isNotEmpty()) {
                            val file = File(attachFile.text)
                            FeedbackSender.attachFileToIssue(newIssueId, file)
                        }
                    } catch (e: IOException) {
                        //TODO: Implement send feedback later if exception is occurred
                        println(e.message)
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun createFeedbackDialogPanel(): DialogPanel {
        val dialogPanel = panel {
            row {
                titleLabel()
            }
            row {
                sectionLabel()
            }
            row {
                cell(isVerticalFlow = true, isFullWidth = true) {
                    feedbackLabel()
                    feedbackTextArea()
                }
            }
            if (customQuestionLabel != null && customQuestionTextArea != null) {
                // JComponent not working with nullable type.
                // Moreover, the forced type conversion from nullable to normal is regarded as unnecessary
                // and removed during auto-formatting.
                val notNullCustomQuestionLabel: JBLabel = customQuestionLabel
                val notNullCustomQuestionTextArea: EditorTextField = customQuestionTextArea
                row {
                    cell(isVerticalFlow = true, isFullWidth = true) {
                        notNullCustomQuestionLabel()
                        notNullCustomQuestionTextArea()
                    }
                }
            }
            row {
                cell(isVerticalFlow = true, isFullWidth = true) {
                    attachFileLabel()
                    attachFile()
                }
            }
        }

        return dialogPanel.apply {
            ui = DarculaPanelUI()
            font = UIManager.getFont("Label.font")
            preferredFocusedComponent = feedbackTextArea
        }
    }

    override fun doValidateAll(): MutableList<ValidationInfo> {
        val validationInfoList = mutableListOf<ValidationInfo>()

        val validationAttachFile = checkAttachFile()
        if (validationAttachFile != null) {
            validationInfoList.add(validationAttachFile)
        }

        if (feedbackTextArea.text.isEmpty()) {
            validationInfoList.add(
                    ValidationInfo(
                            FeedbackBundle.message("dialog.default.validate.description.empty"),
                            feedbackTextArea
                    )
            )
        }

        if (customQuestionTextArea != null) {
            if (customQuestionTextArea.text.isEmpty()) {
                validationInfoList.add(
                        ValidationInfo(
                                FeedbackBundle.message("dialog.default.validate.custom.questions.empty"),
                                customQuestionTextArea
                        )
                )
            }
        }
        return validationInfoList
    }

    private fun checkAttachFile(): ValidationInfo? {
        if (attachFile.text.isNotEmpty()) {
            val file = File(attachFile.text)
            if (!file.exists()) {
                return ValidationInfo(FeedbackBundle.message("dialog.default.validate.attach.file.not.exists"), attachFile)
            } else {
                val fileSize = file.length()
                if (fileSize > FeedbackSender.attachFileMaxSize) {
                    return ValidationInfo(FeedbackBundle.message("dialog.default.validate.attach.file.too.large"), attachFile)
                }
            }
        }
        return null
    }
}