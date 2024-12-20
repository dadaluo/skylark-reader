package cn.luoym.bookreader.skylarkreader.ui


interface ReaderUI  {

    fun showReadContent()

    fun nextPage()

    fun prevPage()

    fun clearReadContent()

    fun updateFontStyle()

    fun isActive(): Boolean

    fun exit()

}