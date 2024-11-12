package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.utils.sendNotify
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import javax.swing.JFileChooser
import javax.swing.JPanel

class BookshelvesUI {

    val bookshelves: JPanel = JPanel(BorderLayout())

    val jbList: JBList<BookState> = JBList<BookState>()

    var currentSelect: BookState? = null

    constructor(){
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, createActionGroup(), true)
        toolbar.targetComponent = bookshelves
        this.bookshelves.add(toolbar.component, BorderLayout.NORTH)

    }

    fun createActionGroup(): ActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(AddBookAction())
        actionGroup.add(RemoveBookAction())
        actionGroup.add(ReadBookAction())
        return actionGroup
    }

    fun refreshShelves() {

    }


    inner class AddBookAction : AnAction("Add Book", "", AllIcons.General.Add) {
        override fun actionPerformed(p0: AnActionEvent) {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, true)
            val project = ProjectManager.getInstance().defaultProject
            val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
            val chooseFile = FileChooser.chooseFiles(descriptor, project, project.workspaceFile){
                it.forEach {
                    bookshelves.addBook(it.path)
                }
            }
            refreshShelves()
        }
    }


    inner class RemoveBookAction : AnAction("Remove Book", "", AllIcons.General.Remove) {
        override fun actionPerformed(p0: AnActionEvent) {
            if (currentSelect == null) {
                sendNotify("请选择要删除的选项", NotificationType.WARNING)
                return
            }
            val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
            bookshelves.removeBook(currentSelect!!)
            refreshShelves()
        }
    }

    inner class ReadBookAction : AnAction("Read Book", "", AllIcons.Toolwindows.ToolWindowRun) {
        override fun actionPerformed(p0: AnActionEvent) {
            if (currentSelect == null) {
                sendNotify("请选择要读的书", NotificationType.WARNING)
                return
            }
        }
    }
}