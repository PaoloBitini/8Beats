package com.example.a8beats

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class InnerFragmentEvents : Fragment(R.layout.inner_fragment_events),
    DatePickerDialog.OnDateSetListener {
    private lateinit var context_: Context
    private lateinit var dateText: TextView
    private lateinit var bandText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var buttonAdd: Button
    private lateinit var queue: RequestQueue
    private lateinit var datePicker: DatePickerFragment
    private val sharedData: SharedDataViewModel by activityViewModels()

    private val respList = Response.Listener<JSONObject> {
        dateText.text = ""
        bandText.text.clear()
        descriptionText.text.clear()
        sharedData.addEvent(it.getJSONObject("event"))
        User.addEvent(it.getJSONObject("event"))
        buttonAdd.isEnabled = true
        Toast.makeText(context_, context_.getString(R.string.event_added), Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateText = view.findViewById(R.id.date)
        bandText = view.findViewById(R.id.band)
        descriptionText = view.findViewById(R.id.description)
        buttonAdd = view.findViewById(R.id.buttonAdd)

        dateText.setOnClickListener {
            datePicker = DatePickerFragment(context_, this)
            datePicker.show(requireActivity().supportFragmentManager, "date picker")
        }

        buttonAdd.setOnClickListener {
            if (User.club.length() > 0) {
                if (descriptionText.text.isNotEmpty() and dateText.text.isNotEmpty() and bandText.text.isNotEmpty()) {
                    buttonAdd.isEnabled = false
                    queue.add(
                        RequestManager.addEvent(
                            context_,
                            User.username,
                            dateText.text.toString(),
                            User.club.getString("name"),
                            bandText.text.toString(),
                            descriptionText.text.toString(),
                            buttonAdd,
                            respList
                        )
                    )
                }
            } else {
                Toast.makeText(context_, getString(R.string.claim_a_club_first), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, dayOfMonth)
        val string = DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
        dateText.text = string
    }
}