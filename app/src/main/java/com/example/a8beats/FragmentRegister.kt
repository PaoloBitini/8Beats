package com.example.a8beats

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FragmentRegister : Fragment(R.layout.fragment_register) {

    private lateinit var register: Button
    private lateinit var login: Button
    private lateinit var user: EditText
    private lateinit var email: EditText
    private lateinit var psw: EditText
    private lateinit var psw2: EditText
    private lateinit var queue: RequestQueue
    private lateinit var listener: FragmentRegisterListener
    private val respList = Response.Listener<JSONObject> { response ->

        register.isEnabled = true

        if (response.getString("result") == "true")
            Toast.makeText(activity, getString(R.string.register_success), Toast.LENGTH_SHORT)
                .show()
        else
            Toast.makeText(activity, getString(R.string.already_registered), Toast.LENGTH_SHORT)
                .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register = view.findViewById(R.id.registerButton)
        login = view.findViewById(R.id.button_backLogin)
        user = view.findViewById(R.id.usernameForm)
        email = view.findViewById(R.id.EmailAddress)
        psw = view.findViewById(R.id.passwordForm)
        psw2 = view.findViewById(R.id.passwordForm2)
        queue = Volley.newRequestQueue(activity)

        register.setOnClickListener {
            if (user.text.toString().length > 15 || user.text.toString().length < 4) {
                Toast.makeText(activity, getString(R.string.invalid_username), Toast.LENGTH_SHORT)
                    .show()
            } else if (psw.text.toString().length < 6 || psw.text.toString().length > 20) {
                Toast.makeText(activity, getString(R.string.invalid_password), Toast.LENGTH_SHORT)
                    .show()
            } else if (psw2.text.toString().length < 6 || psw2.text.toString().length > 20) {
                Toast.makeText(activity, getString(R.string.invalid_password), Toast.LENGTH_SHORT)
                    .show()
            } else if (!psw.text.toString().equals(psw2.text.toString(), true)) {
                Toast.makeText(
                    activity,
                    getString(R.string.passwords_different),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                register.isEnabled = false
                queue.add(
                    RequestManager.register(
                        requireActivity().applicationContext,
                        user.text.toString(),
                        email.text.toString(),
                        psw.text.toString(),
                        register,
                        respList
                    )
                )
            }
        }

        login.setOnClickListener {
            listener.onBackToLoginButtonPressed()
        }
    }

    interface FragmentRegisterListener {
        fun onBackToLoginButtonPressed()
    }

    //se la classe activity a cui il fragment è "attaccato" implementa l'interfaccia, allora listener viene inizializzato al momento dell'attacco
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentRegisterListener) {
            listener = context
        }
    }
}