package cn.luoym.bookreader.skylarkreader.book

import com.intellij.ui.JBIntSpinner
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

abstract class AbstractBook {

    var id: Long = 0

    lateinit var bookName: String

    lateinit var path: String

    var pageIndex: Int = 1
        set(value){
            field = value
            if (spinner != null) {
                spinner!!.value = value
            }
        }

    var maxPageIndex: Int = 1

    var bookType: BookTypeEnum? = null

    var spinner:JBIntSpinner? = null

    fun addPageIndex(int: Int):Boolean{
        val i = pageIndex + int
        if(i >= maxPageIndex || i < 1){
            return false
        }
        this.pageIndex = i
        return true
    }

    open fun readingProgress():String{
        val decimalFormat = DecimalFormat("0.00%")
        val lengthDecimal = BigDecimal(maxPageIndex)
        val indexDecimal = BigDecimal(pageIndex)
        val divide = indexDecimal.divide(lengthDecimal, 4, RoundingMode.HALF_UP)
        return decimalFormat.format(divide)
    }
    abstract fun doRead():String

}