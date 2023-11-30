package com.example.a8beats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import org.json.JSONObject

class FragmentSearch : Fragment(R.layout.fragment_search) {
    private lateinit var context_: Context
    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ClubsListAdapter
    private lateinit var searchText: EditText
    private lateinit var buttonSearch: ImageButton
    private lateinit var searchRequestTask: SearchRequestTask
    private lateinit var searchEngine: SearchEngine
    private lateinit var clubInfo: DialogClubInfo
    private lateinit var clubList: List<JSONObject>
    private lateinit var queue: RequestQueue
    private val sharedData: SharedDataViewModel by activityViewModels()
    private val searchCallback = object : SearchSelectionCallback {

        override fun onError(e: Exception) {
            Toast.makeText(context_, getString(R.string.error_search), Toast.LENGTH_SHORT).show()
        }

        override fun onSuggestions(
            suggestions: List<SearchSuggestion>,
            responseInfo: ResponseInfo
        ) {
            if (suggestions.isNotEmpty()) {
                searchRequestTask = searchEngine.select(suggestions, secondSearchCallback)
            }
        }

        override fun onCategoryResult(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
        }

        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            responseInfo: ResponseInfo
        ) {
        }
    }
    private val secondSearchCallback = object : SearchMultipleSelectionCallback {
        override fun onError(e: Exception) {
            Toast.makeText(context_, getString(R.string.error_search), Toast.LENGTH_SHORT).show()
        }

        override fun onResult(
            suggestions: List<SearchSuggestion>,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            queue.add(RequestManager.get(context_, RequestManager.TYPE_CLUBS, "italia") {

                clubList = Utils.createClubListFromResults(results, it.getJSONArray("clubs"))
                arrayAdapter.setList(clubList)
                sharedData.setData(clubList)
                listView.adapter = arrayAdapter

            })
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngine = MapboxSearchSdk.createSearchEngine()
        arrayAdapter = ClubsListAdapter(context_, listOf())
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        searchText = view.findViewById(R.id.searchText)
        buttonSearch = view.findViewById(R.id.searchButton)
        searchEngine = MapboxSearchSdk.createSearchEngine()

        buttonSearch.setOnClickListener {
            val options = SearchOptions.Builder().limit(5).build()
            searchRequestTask =
                searchEngine.search(searchText.text.toString(), options, searchCallback)
        }

        listView.setOnItemClickListener { _, _, position, _ ->

            clubInfo = DialogClubInfo(queue, clubList[position])
            clubInfo.show(requireActivity().supportFragmentManager, "clubInfo")

        }

        sharedData.data.observe(viewLifecycleOwner, { list ->
            if (list != null) {
                clubList = list
                arrayAdapter.setList(list)
                listView.adapter = arrayAdapter
            }
        })
    }

}