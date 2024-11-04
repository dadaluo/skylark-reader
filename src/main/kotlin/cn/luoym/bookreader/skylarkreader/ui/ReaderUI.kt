package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.book.BookProperties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import java.awt.Font
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea

class ReaderUI {

    private var reader: JPanel? = null

    private var contentPanel: JPanel? = null

    private var btnFirst: JButton? = null

    private var btnPre: JButton? = null

    private var btnNext: JButton? = null

    private var btnLast: JButton? = null

    private var biggerFontSize: JButton? = null

    private var smallerFontSize: JButton? = null

    private var textArea: JTextArea? = null

    private var book: AbstractBook

    private fun createUIComponents() {

    }


    constructor(
        project: Project,
        toolWindow: ToolWindow,
        book: AbstractBook,
    ) {
        this.book = book
        val properties = ApplicationManager.getApplication().getService<BookProperties>(BookProperties::class.java)
        val font = Font(properties.font!!, Font.PLAIN, properties.fontSize.toInt())
        font.deriveFont(properties.fontSize)
        textArea!!.font = font
        btnFirst!!.addActionListener({
            book.index = 1;
            pageChage()
        })

        btnPre!!.addActionListener({
            book.index -=1
            pageChage()
        })

        btnNext!!.addActionListener({
            book.index +=1
            pageChage()
        })

        btnLast!!.addActionListener({
            book.index = book.maxIndex
            pageChage()
        })

        biggerFontSize!!.addActionListener({
            book.fontSize += 0.5F
            fontSizeChange()
        })

        smallerFontSize!!.addActionListener({
            book.fontSize -= 0.5F
            fontSizeChange()
        })

    }

    private fun pageChage() {
        val read = book.doRead()
        textArea!!.text = read
    }

    private fun fontSizeChange() {
        textArea!!.font = textArea!!.font.deriveFont(book.fontSize)
    }

}