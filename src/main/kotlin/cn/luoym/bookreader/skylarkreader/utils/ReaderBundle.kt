package cn.luoym.bookreader.skylarkreader.utils

import cn.luoym.bookreader.skylarkreader.properties.Constants
import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey
import java.util.*

object ReaderBundle {

    private val bundle by lazy { ResourceBundle.getBundle(Constants.BUNDLE_NAME) }

    fun message(@PropertyKey(resourceBundle = Constants.BUNDLE_NAME) key: String, vararg params: Any): String {
        return AbstractBundle.message(bundle, key, params)
    }
}