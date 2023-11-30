package com.example.a8beats

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class InnerFragmentPosts : Fragment(R.layout.inner_fragment_post) {

    private lateinit var context_: Context
    private lateinit var objectText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var buttonAdd: Button
    private lateinit var queue: RequestQueue
    private val sharedData: SharedDataViewModel by activityViewModels()

    private val respList = Response.Listener<JSONObject> {
        objectText.text.clear()
        descriptionText.text.clear()
        sharedData.addPost(it.getJSONObject("post"))
        User.addPost(it.getJSONObject("post"))
        buttonAdd.isEnabled = true
        Toast.makeText(context_, context_.getString(R.string.post_added), Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectText = view.findViewById(R.id.objectText)
        descriptionText = view.findViewById(R.id.description)
        buttonAdd = view.findViewById(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            if (descriptionText.text.isNotEmpty() and objectText.text.isNotEmpty()) {
                buttonAdd.isEnabled = false
                queue.add(
                    RequestManager.addPost(
                        context_,
                        User.username,
                        objectText.text.toString(),
                        descriptionText.text.toString(),
                        buttonAdd,
                        respList
                    )
                )
            }
        }
    }
}