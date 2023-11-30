package com.example.a8beats

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class FragmentPosts : Fragment(R.layout.fragment_posts) {
    private lateinit var context_: Context
    private lateinit var spinner: Spinner
    private lateinit var listView: ListView
    private lateinit var arrayAdapterPosts: ArrayAdapter<String>
    private lateinit var arrayAdapterEvents: ArrayAdapter<String>
    private val sharedData: SharedDataViewModel by activityViewModels()
    lateinit var queue: RequestQueue

    private val posts = InnerFragmentPosts()
    private val events = InnerFragmentEvents()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().supportFragmentManager.commit {
            add(R.id.fragment_container_posts, posts)
            add(R.id.fragment_container_posts, events)
            hide(events)
        }
        listView = view.findViewById(R.id.listView)
        registerForContextMenu(listView)
        arrayAdapterEvents = ArrayAdapter(context_, R.layout.style_list_events_posts)
        arrayAdapterPosts = ArrayAdapter(context_, R.layout.style_list_events_posts)

        User.posts.observe(viewLifecycleOwner, {
            arrayAdapterPosts.clear()
            for (i in 0 until it.count()) {
                arrayAdapterPosts.add(Utils.postJsonToString(context_, it[i]))
            }
                listView.adapter = arrayAdapterPosts
        })

        User.events.observe(viewLifecycleOwner, {
            arrayAdapterEvents.clear()
            for (i in 0 until it.count()) {
                arrayAdapterEvents.add(Utils.eventJsontoString(context_, it[i]))
            }
                listView.adapter = arrayAdapterEvents
        })

        listView.adapter = arrayAdapterPosts

        spinner = view.findViewById(R.id.spinner_type)
        ArrayAdapter.createFromResource(
            context_,
            R.array.postsAndEvents,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    requireActivity().supportFragmentManager.commit {
                        show(posts)
                        hide(events)
                    }
                    listView.adapter = arrayAdapterPosts
                } else {
                    requireActivity().supportFragmentManager.commit {
                        hide(posts)
                        show(events)
                    }
                    listView.adapter = arrayAdapterEvents
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                requireActivity().supportFragmentManager.commit {
                    show(posts)
                    hide(events)
                }
                listView.adapter = arrayAdapterPosts
            }

        }

    }

    override fun onStart() {
        super.onStart()
        if (spinner.selectedItemPosition == 0) {

            requireActivity().supportFragmentManager.commit {
                show(posts)
                hide(events)
            }
        } else {
            requireActivity().supportFragmentManager.commit {
                hide(posts)
                show(events)
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        MenuInflater(context_).inflate(R.menu.delete_context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when (item.itemId) {
            R.id.delete -> {
                val selected = info.position
                if (listView.adapter == arrayAdapterPosts) {
                    queue.add(
                        RequestManager.delPost(
                            context_,
                            User.username,
                            User.getPosts()[selected].getString("id"),
                        ) {
                            sharedData.delPost(selected)
                            User.delPost(selected)
                            Toast.makeText(context_, getString(R.string.post_deleted), Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                } else {
                    queue.add(
                        RequestManager.delEvent(
                            context_,
                            User.username,
                            User.getEvents()[selected].getString("id"),
                        ) {
                            sharedData.delEvent(selected)
                            User.delEvent(selected)
                            Toast.makeText(context_, getString(R.string.event_deleted), Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
            else -> {
            }
        }
        return true
    }
}