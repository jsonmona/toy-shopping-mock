package kr.co.minary.shoppingmock

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneNumberWatcher(private val edit: EditText) : TextWatcher {
    companion object {
        private val REGEX_PHONE = Regex("(^02|^[0-9]{3})([0-9]+)([0-9]{4})")
        private val REGEX_NON_NUMERIC = Regex("[^0-9]")

        fun removeNonDigits(phone: String): String {
            return REGEX_NON_NUMERIC.replace(phone, "")
        }

        fun checkValidity(phone: String) = REGEX_PHONE.matches(removeNonDigits(phone))

        fun formatPhone(phone: String): String {
            val original = removeNonDigits(phone)
            val match = REGEX_PHONE.matchEntire(original) ?: return original

            return StringBuilder().run {
                append(match.groups[1]!!.value)
                append('-')
                append(match.groups[2]!!.value)
                append('-')
                append(match.groups[3]!!.value)
                toString()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null)
            return

        var caret: Int? = null

        if (edit.selectionStart == edit.selectionEnd && edit.selectionStart < s.toString().length)
            caret = edit.selectionStart

        val formatted = formatPhone(s.toString())
        if (s.toString() != formatted) {
            edit.setText(formatted)
            edit.setSelection(caret ?: formatted.length)
        }
    }
}
