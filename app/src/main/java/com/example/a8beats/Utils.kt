package com.example.a8beats

import android.content.Context
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchResult
import org.json.JSONArray
import org.json.JSONObject

class Utils {
    companion object {


        fun createClubListFromResults(
            resultSearch: List<SearchResult>,
            resultDatabase: JSONArray
        ): List<JSONObject> {

            //coordinate dei risultati del database
            val resultDatabaseFeatures = arrayListOf<Feature>()
            for (i in 0 until resultDatabase.length()) {
                val item = resultDatabase.getJSONObject(i)
                resultDatabaseFeatures.add(
                    Feature.fromGeometry(
                        Point.fromLngLat(
                            item.getDouble("lon"),
                            item.getDouble("lat")
                        )
                    )
                )
            }

            //coordinate dei risultati della ricerca
            val features = ArrayList<Feature>()
            for (i in 0 until resultSearch.count()) {
                val item = resultSearch[i]
                features.add(
                    Feature.fromGeometry(
                        Point.fromLngLat(
                            item.coordinate!!.longitude(),
                            item.coordinate!!.latitude()
                        )
                    )
                )
            }

            //mapping dei valori comuni dei due risultati
            val index = hashMapOf<Int, Int>()
            for (elem in features) {
                if (resultDatabaseFeatures.contains(elem)) {
                    index[(features.indexOf(elem))] = resultDatabaseFeatures.indexOf(elem)
                }
            }

            //create clubs List
            val clubList = arrayListOf<JSONObject>()
            for (i in resultSearch) {
                var club = JSONObject()
                if (index.containsKey(resultSearch.indexOf(i))) {
                    club = resultDatabase.getJSONObject(index[resultSearch.indexOf(i)]!!)
                    club.put("lat", i.coordinate!!.latitude())
                        .put("lon", i.coordinate!!.longitude())
                        .put("distance", i.distanceMeters?.toInt()?.div(1000).toString() + "km")
                } else {
                    club.put("name", i.name)
                        .put("address", i.address!!.formattedAddress())
                        .put("descr", "no description")
                        .put("owner", "none")
                        .put("lat", i.coordinate!!.latitude())
                        .put("lon", i.coordinate!!.longitude())
                        .put("distance", i.distanceMeters?.toInt()?.div(1000).toString() + "km")
                }
                clubList.add(club)
            }
            return clubList.toList()
        }


        fun friendJsonToString(context: Context, friend: JSONObject): String {
            var text = context.getString(R.string.nickname)
                .plus(friend.getString("name"))
                .plus("\n\n")
                .plus(context.getString(R.string.email_2))
                .plus(friend.getString("email"))
                .plus("\n\n")
                .plus(context.getString(R.string.bands_2))
            val bands = friend.getJSONArray("bands")
            if (bands.length() > 0) {
                for (j in 0 until bands.length()) {
                    text = text.plus("\n")
                        .plus(context.getString(R.string.member_of))
                        .plus(bands.getJSONObject(j).getString("band"))
                        .plus(" ").plus(context.getString(R.string.as_)).plus(" ")
                        .plus(bands.getJSONObject(j).getString("role"))
                }
            } else {
                text = text.plus("none")
            }
            text = text.plus("\n\n").plus(context.getString(R.string.club_2))
            val club = friend.getJSONObject("club")
            text = if (club.length() > 0) {
                text.plus("\n")
                    .plus(club.getString("name"))
                    .plus("\n")
                    .plus(club.getString("address"))
            } else {
                text.plus("none")
            }
            return text
        }

        fun showBand(context: Context, it: JSONObject): String {
            var text = context.getString(R.string.name_2)
                .plus("\n\t")
                .plus(it.getString("name"))
                .plus("\n\n")
                .plus(context.getString(R.string.genre_2))
                .plus("\n\t")
                .plus(it.getString("genre"))
                .plus("\n\n")
                .plus(context.getString(R.string.country_2))
                .plus("\n\t")
                .plus(it.getString("country"))
                .plus("\n\n")
                .plus(context.getString(R.string.city_2))
                .plus("\n\t")
                .plus(it.getString("city"))
                .plus("\n\n")
                .plus(context.getString(R.string.members))

            val members = it.getJSONArray("members")
            for (j in 0 until members.length()) {
                text = text
                    .plus("\n\t")
                    .plus(members.getJSONObject(j).getString("name"))
                    .plus(" ")
                    .plus(context.getString(R.string.as_))
                    .plus(" ")
                    .plus(members.getJSONObject(j).getString("role"))
            }
            text = text
                .plus("\n\n")
                .plus(context.getString(R.string.description))
                .plus("\n")
                .plus(it.getString("descr"))
            return text
        }

        fun fromJsonArrayToList(array: JSONArray): ArrayList<JSONObject> {
            val list = ArrayList<JSONObject>()
            for (i in 0 until array.length()) {
                list.add(array.getJSONObject(i))
            }
            return list
        }

        fun postJsonToString(context : Context, post: JSONObject): String {
            return context.getString(R.string.requested_by)
                .plus(post.getString("user"))
                .plus("\n")
                .plus(context.getString(R.string.object_))
                .plus(post.getString("object"))
                .plus("\n")
                .plus(context.getString(R.string.description))
                .plus(post.getString("descr"))
        }

        fun eventJsontoString(context : Context, event: JSONObject): String {
            return context.getString(R.string.date)
                .plus(event.getString("date"))
                .plus("\n")
                .plus(context.getString(R.string.at))
                .plus(event.getString("club"))
                .plus("\n")
                .plus(event.getString("band"))
                .plus(context.getString(R.string.in_concert))
                .plus("\n")
                .plus(context.getString(R.string.more_info))
                .plus(event.getString("descr"))
        }

    }
}