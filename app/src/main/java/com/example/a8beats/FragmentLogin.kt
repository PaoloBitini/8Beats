package com.example.a8beats

import android.content.Context
import android.content.Intent
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

class FragmentLogin : Fragment(R.layout.fragment_login) {

    private lateinit var listener: FragmentLoginListener
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var userTx: EditText
    private lateinit var psw: EditText
    private lateinit var queue: RequestQueue
    private val respList = Response.Listener<JSONObject> {

        login.isEnabled = true

        if (it.getString("result") == "logged") {
            Toast.makeText(
                activity,
                context?.getString(R.string.logged),
                Toast.LENGTH_SHORT
            ).show()
            val new = Intent(context, ActivityProfile::class.java)
            User.setUser(it)
            startActivity(new)
        } else {
            Toast.makeText(
                context,
                context?.getString(R.string.cannot_login) + it.getString("result").toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         login = view.findViewById(R.id.loginButton)
         register = view.findViewById(R.id.registerButton)
         userTx = view.findViewById(R.id.usernameForm)
         psw = view.findViewById(R.id.passwordForm)
         queue = Volley.newRequestQueue(activity)

        login.setOnClickListener {
            login.isEnabled = false
            queue.add(
                RequestManager.login(
                    requireActivity().applicationContext,
                    userTx.text.toString(),
                    psw.text.toString(),
                    login,
                    respList
                )
            )
        }

        register.setOnClickListener {
            listener.onRegisterButtonPressed()
        }
    }

    interface FragmentLoginListener {
        fun onRegisterButtonPressed()
    }

    //se la classe activity a cui il fragment è "attaccato" implementa l'interfaccia, allora listener viene inizializzato al momento dell'attacco
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentLoginListener) {
            listener = context
        }
    }
}
