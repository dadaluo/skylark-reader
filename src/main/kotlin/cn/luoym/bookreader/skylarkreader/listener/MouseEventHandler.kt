package cn.luoym.bookreader.skylarkreader.listener

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.function.Consumer

class MouseEventHandler : MouseListener {

    var mouseClicked:Consumer<MouseEvent>? = null

    var mousePressed:Consumer<MouseEvent>? = null

    var mouseReleased:Consumer<MouseEvent>? = null

    var mouseEntered:Consumer<MouseEvent>? = null

    var mouseExited:Consumer<MouseEvent>? = null


    override fun mouseClicked(e: MouseEvent) {
        mouseClicked?.accept(e)
    }

    override fun mousePressed(e: MouseEvent) {
        mousePressed?.accept(e)
    }

    override fun mouseReleased(e: MouseEvent) {
        mouseReleased?.accept(e)
    }

    override fun mouseEntered(e: MouseEvent) {
        mouseEntered?.accept(e)
    }

    override fun mouseExited(e: MouseEvent) {
        mouseExited?.accept(e)
    }
}