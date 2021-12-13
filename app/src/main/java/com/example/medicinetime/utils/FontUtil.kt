package com.example.medicinetime.utils

import com.example.medicinetime.MedicineApp
import java.lang.Exception
import java.util.*

object FontUtil {
    const val ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf"
    const val ROBOTO_LIGHT = "fonts/Roboto-Light.ttf"
    const val ROBOTO_BOLD = "fonts/Roboto-Bold.ttf"

    // Cache fonts in hash table
    private val fontCache: Hashtable<String, Typeface?> = Hashtable<String, Typeface?>()
    fun getTypeface(name: String): Typeface? {
        var tf: Typeface? = fontCache[name]
        if (tf == null) {
            tf = try {
                Typeface.createFromAsset(MedicineApp.getInstance().getAssets(), name)
            } catch (e: Exception) {
                return null
            }
            fontCache[name] = tf
        }
        return tf
    }
}