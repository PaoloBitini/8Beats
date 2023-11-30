package com.example.a8beats

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FragmentBoard : Fragment(R.layout.fragment_board) {

    private lateinit var context_: Context
    private lateinit var listView: ListView
    private lateinit var arrayAdapterPosts: ArrayAdapter<String>
    private lateinit var arrayAdapterEvents: ArrayAdapter<String>
    private lateinit var buttonPosts: Button
    private lateinit var buttonEvents: Button
    private val sharedData: SharedDataViewModel by activityViewModels()
    private var currentShowedIsEvents = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        buttonPosts = view.findViewById(R.id.buttonPostings)
        buttonEvents = view.findViewById(R.id.buttonEvents)
        arrayAdapterEvents = ArrayAdapter(context_, R.layout.style_list_events_posts)
        arrayAdapterPosts = ArrayAdapter(context_, R.layout.style_list_events_posts)

        sharedData.events.observe(viewLifecycleOwner, {
            arrayAdapterEvents.clear()
            for (i in it) {
                arrayAdapterEvents.add(Utils.eventJsontoString(context_, i))
            }
            if (currentShowedIsEvents) {
                listView.adapter = arrayAdapterEvents
            }
        })

        sharedData.posts.observe(viewLifecycleOwner, {
            arrayAdapterPosts.clear()
            for (i in it) {
                arrayAdapterPosts.add(Utils.postJsonToString(context_, i))
            }
            if (!currentShowedIsEvents) {
                listView.adapter = arrayAdapterPosts
            }
        })

        buttonEvents.setOnClickListener {
            listView.adapter = arrayAdapterEvents
            currentShowedIsEvents = true
        }

        buttonPosts.setOnClickListener {
            listView.adapter = arrayAdapterPosts
            currentShowedIsEvents = false
        }
    }
}