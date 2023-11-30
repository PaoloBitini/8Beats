package com.example.a8beats

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.a8beats.R.id.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class ActivityProfile : AppCompatActivity() {
    private val map = FragmentMap()
    private val search = FragmentSearch()
    private val board = FragmentBoard()
    private val club = FragmentClub()
    private val posts = FragmentPosts()
    private val messages = FragmentMessages()
    private val friends = FragmentFriends()
    private val bands = FragmentBands()
    private val sharedData: SharedDataViewModel by viewModels()
    private lateinit var volley: RequestQueue
    private lateinit var bar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var profButton: ImageButton
    private lateinit var messageButton: ImageButton
    private lateinit var friendsButton: ImageButton
    private lateinit var bandsButton: ImageButton
    private lateinit var layoutHeader: LinearLayout
    private lateinit var nav: BottomNavigationView

    private val respList = Response.Listener<JSONObject> {
        sharedData.setEvents(Utils.fromJsonArrayToList(it.getJSONArray("events")).toMutableList())
        sharedData.setPosts(Utils.fromJsonArrayToList(it.getJSONArray("posts")).toMutableList())
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        volley = Volley.newRequestQueue(this)
        bar = findViewById(toolbar)
        drawerLayout = findViewById(layout_drawer)
        navView = findViewById(nav_drawer)
        setSupportActionBar(bar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //get events and posts data from api
        volley.add(RequestManager.get(this, RequestManager.TYPE_POSTS_EVENTS, "", respList))

        //set nav drawer menu
        navView.bringToFront()
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                create_band -> createBand(volley)
                join_band -> joinBand(volley)
                leave_band -> {
                    if (User.getBands().count() > 0) {
                        leaveBand(volley)
                    } else {
                        Toast.makeText(this, getString(R.string.not_joining), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                clubs -> clubButton(volley)
                events_and_posts -> postsEventsButton()
                logout -> finish()  // go back to the previous activity
            }
            true
        }


        //set the nav drawer header
        val navHeaderView =
            navView.inflateHeaderView(R.layout.navigation_header) //to set the text of nav. drawer i have to "set" the layout of the header or you get null pointer exception
        navHeaderView.findViewById<TextView>(profile_username).text = User.username
        navHeaderView.findViewById<TextView>(profile_email).text = User.email
        layoutHeader = navHeaderView.findViewById(innerLayout)

        //set the profile button
        profButton = findViewById(profile_button)
        profButton.setOnClickListener {
            layoutHeader.removeAllViews()
            User.bands.observe(this, {
                val bands = User.getBands()
                layoutHeader.removeAllViews()
                if (User.getBands().count() != 0) {
                    val record = TextView(this)
                    record.text = "Bands"
                    record.textSize = 20.0f
                    record.setTextColor(Color.BLUE)
                    record.setPadding(20, 0, 20, 10)
                    layoutHeader.addView(record)
                    for (i in 0 until bands.count()) {
                        val record = TextView(this)
                        record.text =
                            getString(R.string.member_of).plus(" ").plus(bands[i].getString("band"))
                                .plus(" ").plus(getString(R.string.as_)).plus(" ")
                                .plus(bands[i].getString("role"))
                        if (i == bands.count() - 1) {
                            record.setPadding(20, 0, 20, 30)
                        } else {
                            record.setPadding(20, 0, 20, 10)
                        }
                        layoutHeader.addView(record)
                    }
                }
            })
            drawerLayout.openDrawer(navView)
        }

        //set the message button
        messageButton = findViewById(R.id.messageButton)
        messageButton.setOnClickListener {
            hideAllAndShow(messages)

        }

        //set the friends button
        friendsButton = findViewById(R.id.friendsButton)
        friendsButton.setOnClickListener {
            hideAllAndShow(friends)

        }

        //set the bands button
        bandsButton = findViewById(R.id.bandsButton)
        bandsButton.setOnClickListener {
            hideAllAndShow(bands)
        }


        //set the fragment container default fragment (map)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(fragment_container_view, map, "map")
                add(fragment_container_view, search, "search")
                add(fragment_container_view, board, "board")
                add(fragment_container_view, messages, "messages")
                add(fragment_container_view, friends, "friends")
                add(fragment_container_view, bands, "bands")
                add(fragment_container_view, posts, "posts")
                hide(posts)
                hide(search)
                hide(board)
                hide(messages)
                hide(friends)
                hide(bands)
                show(map)
            }
        }


        //set the bottom navigation toolbar and the action for every button
        nav = findViewById(bottomNavigationView)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                page_search -> {
                    hideAllAndShow(search)
                    true
                }
                page_map -> {
                    hideAllAndShow(map)
                    true
                }
                page_board -> {
                    hideAllAndShow(board)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    //set the bands button in the profile section (drawer layout)
    private fun leaveBand(queue: RequestQueue) {
        val dialog = DialogBandLeave(queue)
        dialog.show(supportFragmentManager, "Leave a band")
    }

    private fun joinBand(queue: RequestQueue) {
        val dialog = DialogBandJoin(queue)
        dialog.show(supportFragmentManager, "Join a band")
    }

    private fun createBand(queue: RequestQueue) {
        val dialog = DialogBandCreate(queue)
        dialog.show(supportFragmentManager, "Create a new band")
    }

    //set the club button in the profile section (drawer layout)
    private fun clubButton(queue: RequestQueue) {
        if (User.club.isNull("name")) {
            Toast.makeText(this, getString(R.string.claim_a_club_first), Toast.LENGTH_SHORT).show()
            val dialog = DialogClubClaim(queue)
            dialog.show(supportFragmentManager, "Claim a club")
        } else {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (supportFragmentManager.findFragmentByTag("club") != null) {
                hideAllAndShow(club)
            } else {
                supportFragmentManager.commit {
                    add(fragment_container_view, club, "club")
                }
                hideAllAndShow(club)
            }

        }
    }

    //set the posts and events button in the profile section (drawer layout)

    private fun postsEventsButton() {
        drawerLayout.closeDrawer(GravityCompat.START)
        hideAllAndShow(posts)
    }

    override fun onBackPressed() {}

    //for hide all the fragments and show only one
    private fun hideAllAndShow(frag: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            for (i in supportFragmentManager.fragments) {
                hide(i)
            }
            show(frag)
        }
    }
}
