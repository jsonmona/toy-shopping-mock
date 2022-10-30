package kr.co.minary.shoppingmock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.annotation.RawRes

class RegisterFragment : Fragment(R.layout.fragment_register) {
    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        prepareToS(root.findViewById(R.id.register_tos_1_content), R.raw.tos_1)
        prepareToS(root.findViewById(R.id.register_tos_2_content), R.raw.tos_2)

        root.findViewById<EditText>(R.id.register_phone).let {
            it.addTextChangedListener(PhoneNumberWatcher(it))
        }

        root.findViewById<Button>(R.id.register_submit).setOnClickListener {
            val activity = activity ?: return@setOnClickListener

            val check1 = root.findViewById<RadioButton>(R.id.register_tos_check_1).isChecked
            val check2 = root.findViewById<RadioButton>(R.id.register_tos_check_2).isChecked
            val username = root.findViewById<EditText>(R.id.register_username).text.toString()
            val password = root.findViewById<EditText>(R.id.register_password).text.toString()
            val passwordRe = root.findViewById<EditText>(R.id.register_password_re).text.toString()
            val name = root.findViewById<EditText>(R.id.register_name).text.toString()
            val phone = root.findViewById<EditText>(R.id.register_phone).text.toString()
            val address = root.findViewById<EditText>(R.id.register_address).text.toString()

            if (!check1 || !check2) {
                Toast.makeText(activity, R.string.register_toast_tos, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != passwordRe) {
                Toast.makeText(activity, R.string.register_toast_password_diff, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(activity, R.string.register_toast_name_empty, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            if (!PhoneNumberWatcher.checkValidity(phone)) {
                Toast.makeText(activity, R.string.register_toast_invalid_phone, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val appState = AppState.getInstance(activity)

            when (appState.tryRegister(username, password, name, phone, address)) {
                RegisterResult.OK -> {
                    Toast.makeText(activity, R.string.register_toast_ok, Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStack()
                }
                RegisterResult.UNKNOWN -> {
                    Toast.makeText(activity, R.string.register_toast_unknown, Toast.LENGTH_LONG)
                        .show()
                }
                RegisterResult.ID_COLLISION -> {
                    Toast.makeText(
                        activity,
                        R.string.register_toast_id_collision,
                        Toast.LENGTH_LONG
                    ).show()
                }
                RegisterResult.PASSWORD_TOO_WEAK -> {
                    Toast.makeText(
                        activity,
                        R.string.register_toast_password_rule,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 스크롤을 위한 터치 리스너이므로 린트를 무시함
    @SuppressLint("ClickableViewAccessibility")
    private fun prepareToS(it: TextView, @RawRes textId: Int) {
        it.movementMethod = ScrollingMovementMethod()
        it.text = requireContext().resources.openRawResource(textId).bufferedReader()
            .use { reader -> reader.readText() }
        it.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    // It is required to call performClick() in onTouch event.
                    it.performClick()
                }
            }
            false
        }
    }

    override fun onStop() {
        super.onStop()

        (activity as? WelcomeActivity)?.notifyRegisterFragmentClosed()
    }
}
