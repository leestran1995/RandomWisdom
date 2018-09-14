package services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.example.archer.quickwisdom.R
import com.example.archer.quickwisdom.activities.MainActivity
import com.example.archer.quickwisdom.activities.MyBroadcastReceiver
import com.google.gson.GsonBuilder
import constants.WIKI_RANDOM_INTENT
import okhttp3.*
import java.io.IOException

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "services.action.FOO"
private const val ACTION_BAZ = "services.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "services.extra.PARAM1"
private const val EXTRA_PARAM2 = "services.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class WikiIntentService : IntentService("WikiIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        fetchWikiTitle()
    }

    fun fetchWikiTitle() {
        println("Attempting to fetch title")

        val url = "https://en.wikipedia.org/api/rest_v1/page/random/title"
        val request = Request.Builder().url(url).build()   // Create a request object
        val client = OkHttpClient()     // Create a new client\

        var title: String = "Bicycle"
        var summary: String = "blank summary"
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val WikiFeed: WikiResponse = gson.fromJson(body, WikiResponse::class.java)
                title = WikiFeed.items[0].title
                fetchWikiExtract(title)
            }
        })
    }

    fun fetchWikiExtract(title: String) {
        println("Attempting to fetch extract")
        val url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + title
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val PageResponse: PageResponse = gson.fromJson(body, PageResponse::class.java)
                val extract = PageResponse.extract
                val outgoingIntent: Intent = Intent(WIKI_RANDOM_INTENT)
                outgoingIntent.putExtra("title", title)
                outgoingIntent.putExtra("extract", extract)
                println("Attempting to send broadcast")
                sendBroadcast(outgoingIntent)
            }
        })
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
        TODO("Handle action Foo")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, WikiIntentService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, WikiIntentService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}


class WikiResponse(val items: List<Item>)

class Item(val title: String)

class PageResponse(val title: String, val extract: String)

