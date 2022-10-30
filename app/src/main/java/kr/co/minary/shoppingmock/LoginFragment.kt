package kr.co.minary.shoppingmock

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        root.findViewById<Button>(R.id.login_btn).setOnClickListener {
            val context = context ?: return@setOnClickListener

            val id = root.findViewById<EditText>(R.id.login_id).text.toString()
            val pw = root.findViewById<EditText>(R.id.login_pw).text.toString()

            if (AppState.getInstance(context).tryLogin(id, pw)) {
                switchToMain()
            } else {
                Toast.makeText(context, R.string.login_wrong_id_or_pw, Toast.LENGTH_SHORT).show()
            }
        }

        root.findViewById<Button>(R.id.login_register_btn).setOnClickListener {
            val parent = (activity as WelcomeActivity?) ?: return@setOnClickListener
            parent.openRegisterFragment()
        }

        root.findViewById<View>(R.id.login_skip).setOnClickListener {
            switchToMain()
        }
    }

    private fun switchToMain() {
        val parent = activity ?: return
        val intent = Intent(parent, MainActivity::class.java)
        startActivity(intent)
        if (AppState.getInstance(parent).isLoggedIn()) {
            parent.finish()
        }
    }
}
