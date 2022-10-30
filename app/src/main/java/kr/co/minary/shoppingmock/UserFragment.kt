package kr.co.minary.shoppingmock

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged

class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var state: AppState
    private lateinit var toolbar: Toolbar
    private lateinit var fieldId: EditText
    private lateinit var fieldName: EditText
    private lateinit var fieldPhone: EditText
    private lateinit var fieldAddress: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state = AppState.getInstance(requireContext())
        toolbar = view.findViewById(R.id.toolbar)
        fieldId = view.findViewById(R.id.user_id_edit)
        fieldName = view.findViewById(R.id.user_name_edit)
        fieldPhone = view.findViewById(R.id.user_phone_edit)
        fieldAddress = view.findViewById(R.id.user_address_edit)

        fieldId.isEnabled = false
        fieldPhone.addTextChangedListener(PhoneNumberWatcher(fieldPhone))

        view.findViewById<Button>(R.id.user_logout).setOnClickListener {
            val activity = activity ?: return@setOnClickListener
            state.logout()
            Toast.makeText(activity, R.string.user_logout_success, Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        refreshFields(false)
    }

    override fun onPause() {
        super.onPause()

        if(state.isLoggedIn())
            refreshFields(true)
    }

    private fun refreshFields(applyUpdate: Boolean) {
        if (applyUpdate) {
            state.updateUser(
                null,
                fieldName.text.toString(),
                PhoneNumberWatcher.removeNonDigits(fieldPhone.text.toString()),
                fieldAddress.text.toString()
            )
        }

        fieldId.setText(state.getLoginId())
        fieldName.setText(state.getName())
        fieldPhone.setText(PhoneNumberWatcher.formatPhone(state.getPhone()))
        fieldAddress.setText(state.getAddress())
    }
}