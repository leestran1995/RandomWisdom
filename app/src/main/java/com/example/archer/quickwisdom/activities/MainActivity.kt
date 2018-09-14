package com.example.archer.quickwisdom.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.example.archer.quickwisdom.R
import constants.WIKI_RANDOM_INTENT
import services.WikiIntentService


class MainActivity : AppCompatActivity() {
    private lateinit var br: MyBroadcastReceiver
    private lateinit var titleField: TextView
    private lateinit var extractField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleField = findViewById(R.id.titleView)
        extractField = findViewById(R.id.extractView)

        br = MyBroadcastReceiver(this, Handler(), titleField, extractField)
        val filter = IntentFilter(WIKI_RANDOM_INTENT)
        this.registerReceiver(br, filter)
    }

    fun onWikiButtonPress(view: View) {
        val intent = Intent(this, WikiIntentService::class.java)
        startService(intent)
    }

    fun onClearButtonPress(view: View) {
        titleField.text = ""
        extractField.text = ""
    }
}

class MyBroadcastReceiver(
        _context: Context,
        _handler: Handler,
        _titleView: TextView,
        _extractView: TextView
) : BroadcastReceiver() {
    var handler: Handler? = null
    var context: Context? = null
    var titleView: TextView? = null
    var extractView: TextView? = null
    init {
        handler = _handler
        context = _context
        titleView = _titleView
        extractView = _extractView
        println("Creating broadcast receiver")
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Received intent")
        handler?.post {
            titleView?.text = intent?.getStringExtra("title")?.replace("_"," ")
            extractView?.text = intent?.getStringExtra("extract")
        }
    }
}