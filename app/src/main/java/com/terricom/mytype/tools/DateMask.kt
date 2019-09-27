package com.terricom.mytype.tools

import android.text.Editable
import android.text.TextWatcher
import java.util.*


class DateMask : TextWatcher {

    private var updatedText: String? = null
    private var editing: Boolean = false


    override fun beforeTextChanged(
        charSequence: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {

    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.toString() == updatedText || editing) return

        var digits = text.toString().replace("\\D".toRegex(), "")
        val length = digits.length

        if (length <= MIN_LENGTH) {
            updatedText = digits
            return
        }

        if (length > MAX_LENGTH) {
            digits = digits.substring(0, MAX_LENGTH)
        }

        if (length <= 4) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2)

            updatedText = String.format(Locale.US, "%s-%s", month, day)
        } else {
            val year = digits.substring(0 ,4)
            val month = digits.substring(4,6)
            val day = digits.substring(6)


            updatedText = String.format(Locale.US, "%s-%s-%s", year, month, day)
        }
    }

    override fun afterTextChanged(editable: Editable) {

        if (editing) return

        editing = true

        editable.clear()
        editable.insert(0, updatedText)

        editing = false
    }

    companion object {

        private val MAX_LENGTH = 8
        private val MIN_LENGTH = 2
    }

}