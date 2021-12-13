package com.example.medicinetime.views

import android.content.Context
import android.util.AttributeSet
import com.example.medicinetime.utils.FontUtil

class RobotoLightTextView : AppCompatTextView {
    constructor(context: Context?) : super(context) {
        applyCustomFont()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        applyCustomFont()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        applyCustomFont()
    }

    private fun applyCustomFont() {
        val customFont: Typeface = FontUtil.getTypeface(FontUtil.ROBOTO_LIGHT)
        setTypeface(customFont)
    }
}