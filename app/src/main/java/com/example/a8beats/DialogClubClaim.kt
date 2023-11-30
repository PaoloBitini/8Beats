package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.JsonObject
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import org.json.JSONObject


class DialogClubClaim(private val queue: RequestQueue) : DialogFragment() {

    lateinit var context_ : Context
    lateinit var listView : ListView
    lateinit var searchEngine: SearchEngine
    lateinit var searchText : EditText
    lateinit var buttonSearch : ImageButton
    lateinit var searchRequestTask : SearchRequestTask
    lateinit var adapter: ArrayAdapter<String>
    lateinit var storedResults: List<SearchResult>
    private val respList = Response.Listener<JSONObject> {
        if(it.getString("status") == "1") {
            Toast.makeText(context_, context_.getString(R.string.club_claimed), Toast.LENGTH_SHORT).show()
            User.club = it.getJSONObject("club")
        }else{
            Toast.makeText(context_, context_.getString(R.string.clun_already_claimed), Toast.LENGTH_SHORT).show()
        }
      }
    private val searchCallback = object : SearchSelectionCallback {

        override fun onError(e: Exception) {
            Toast.makeText(context_, context_.getString(R.string.error_search), Toast.LENGTH_SHORT).show()
        }

        override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
            if(suggestions.isNotEmpty()){
                searchRequestTask = searchEngine.select(suggestions, secondSearchCallback)
            }
        }
        override fun onCategoryResult(suggestion: SearchSuggestion, results: List<SearchResult>, responseInfo: ResponseInfo) {}
        override fun onResult(suggestion: SearchSuggestion, result: SearchResult, responseInfo: ResponseInfo) {}
    }
    private val secondSearchCallback = object : SearchMultipleSelectionCallback {
        override fun onError(e: Exception) {
            Toast.makeText(context_, context_.getString(R.string.error_search), Toast.LENGTH_SHORT).show()
        }

        override fun onResult(suggestions: List<SearchSuggestion>, results: List<SearchResult>, responseInfo: ResponseInfo) {
            adapter.clear()
            storedResults = results
            val iterator = results.iterator()
            while(iterator.hasNext()){
                val item = iterator.next()
                adapter.add( item.name  /*+ "\n" + item.address?.formattedAddress()*/)
            }
            listView.adapter = adapter
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context_ = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialogBuilder = AlertDialog.Builder(activity)
        val view = layoutInflater.inflate(R.layout.dialog_claim_club, null, false)

        listView = view.findViewById(R.id.listView)
        adapter = ArrayAdapter(context_, android.R.layout.simple_list_item_single_choice )
        searchText = view.findViewById(R.id.searchText)
        buttonSearch = view.findViewById(R.id.searchButton)
        searchEngine = MapboxSearchSdk.createSearchEngine()

        buttonSearch.setOnClickListener{
            val options = SearchOptions.Builder().limit(5).build()
            searchRequestTask = searchEngine.search( searchText.text.toString(), options, searchCallback)
        }

        alertDialogBuilder.setView(view)
        alertDialogBuilder.setTitle(getString(R.string.claim_club))
        alertDialogBuilder.setPositiveButton(getString(R.string.claim)){ _: DialogInterface, _: Int ->

            var item : SearchResult? = null
            for (i in 0 until storedResults.count()){
                if(listView.isItemChecked(i)){
                    item = storedResults[i]
                    break
                }
            }
            if(item != null) {
                queue.add(RequestManager.clubClaim(context_,
                    item.name,
                    item.address!!.formattedAddress()!!,
                    item.address!!.country!!,
                    item.coordinate!!.latitude(),
                    item.coordinate!!.longitude(),
                    respList
                    )
                )
            }
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)){ _: DialogInterface, _: Int -> }

        return alertDialogBuilder.create()
    }
}