package kr.co.minary.shoppingmock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit

class WelcomeActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_REGISTER = "kr.co.minary.shoppingmock.GoRegisterDirectly"
    }

    private var hasRegisterOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null && intent.getBooleanExtra(EXTRA_REGISTER, false))
            openRegisterFragment()
    }

    fun notifyRegisterFragmentClosed() {
        hasRegisterOpened = false
    }

    fun openRegisterFragment() {
        if (hasRegisterOpened)
            return

        val register = RegisterFragment()
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            replace(R.id.fragment_container, register)
            addToBackStack(null)
        }
        hasRegisterOpened = true
    }
}
