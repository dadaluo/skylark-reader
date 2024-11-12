package cn.luoym.bookreader.skylarkreader.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager

class Extensions {
}

fun sendNotify(message: String, type: NotificationType) {
    val project = ProjectManager.getInstance().defaultProject
    NotificationGroupManager.getInstance()
        .getNotificationGroup("skylark-reader-notification")
        .createNotification(message, type)
        .notify(project)
}