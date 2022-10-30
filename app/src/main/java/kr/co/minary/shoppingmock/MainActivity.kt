package kr.co.minary.shoppingmock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navbar: BottomNavigationView

    private var fragmentPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navbar = findViewById(R.id.bottom_navigation)

        val state = AppState.getInstance(this)
        try {
            val name = state.getName()
            val welcomeMsg = String.format(getString(R.string.main_hello), name)
            Toast.makeText(this, welcomeMsg, Toast.LENGTH_SHORT).show()
        } catch(_: NotLoggedInException) {
            // ignore
        }

        switchToFragment(0)

        navbar.setOnNavigationItemSelectedListener { item ->
            val prevItemId = navbar.selectedItemId

            when (item.itemId) {
                R.id.navbar_item_shopping -> {
                    switchToFragment(0)
                    true
                }
                R.id.navbar_item_add -> {
                    switchToFragment(2)
                    true
                }
                R.id.navbar_item_user -> {
                    if (state.isLoggedIn())
                        switchToFragment(3)
                    else {
                        AlertDialog.Builder(this)
                            .setMessage(R.string.main_alert_ask_register)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                val intent = Intent(this, WelcomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.putExtra(WelcomeActivity.EXTRA_REGISTER, true)
                                startActivity(intent)
                            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                dialog.cancel()
                            }.setOnCancelListener {
                                if (prevItemId != R.id.navbar_item_user)
                                    navbar.selectedItemId = prevItemId
                                else {
                                    navbar.selectedItemId = R.id.navbar_item_shopping
                                    switchToFragment(0)
                                }
                            }.show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun showShoppingPage() {
        navbar.selectedItemId = R.id.navbar_item_shopping
        switchToFragment(0)
    }

    private fun switchToFragment(pos: Int) {
        supportFragmentManager.commit {
            if (fragmentPosition < 0) {
                // 새로 시작하는 중에는 애니메이션 없음
            } else if (fragmentPosition == pos) {
                // 같은곳으로 이동하지 않음
                return
            } else if (fragmentPosition < pos) {
                setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            } else {
                setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

            val target = when (pos) {
                0 -> ShopFragment()
                2 -> AddProductFragment()
                3 -> UserFragment()
                else -> return
            }

            replace(R.id.fragment_container, target)
        }

        fragmentPosition = pos
    }
}