package com.ayadiyulianto.dailycoding.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.ayadiyulianto.dailycoding.R

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Menambahkan text aligmnet pada editText
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {

        // Menambahkan aksi ketika ada perubahan text
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Email validation
                error =
                    if (s.toString().isNotEmpty() && s.toString().length >= 8) {
                        null
                    } else {
                        context.getString(R.string.password_less_than_8_chars)
                    }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
}