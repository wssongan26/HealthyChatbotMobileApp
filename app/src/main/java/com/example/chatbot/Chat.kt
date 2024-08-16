package com.example.chatbot

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.Message
import com.example.chatbot.utils.BotResponse
import com.example.chatbot.utils.Constant.OPEN_GOOGLE
import com.example.chatbot.utils.Constant.OPEN_SEARCH
import com.example.chatbot.utils.Constant.RECEIVE_ID
import com.example.chatbot.utils.Constant.SEND_ID
import com.example.chatbot.utils.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Chat : AppCompatActivity() {

    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("Peter", "fan")
    private lateinit var btn_send: Button
    private lateinit var et_message: EditText
    private lateinit var rv_messages: RecyclerView
    private lateinit var iv_back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Find RecyclerView and ImageView in the layout
        rv_messages = findViewById(R.id.rv_messages)
        btn_send = findViewById(R.id.btn_send)
        et_message = findViewById(R.id.et_message)
        iv_back = findViewById(R.id.iv_back)

        // Set onClickListener for the back button
        iv_back.setOnClickListener {
            onBackPressed()
        }

        recycleView()
        clickEvents()

        val random = (0..1).random()
        customMessage("Hello Today you are speaking with ${botList[random]}, how can I help?")
    }

    private fun clickEvents() {
        btn_send.setOnClickListener {
            sendMessage()
        }

        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    private fun recycleView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            et_message.setText("")
            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)
            botResponse(message)
        }


    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)

            val response = BotResponse.processInput(message)

            adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            when (response) {
                OPEN_GOOGLE -> {
                    val site = Intent(Intent.ACTION_VIEW)
                    site.data = Uri.parse("https://www.google.com")
                    startActivity(site)
                }
                OPEN_SEARCH -> {
                    val site = Intent(Intent.ACTION_VIEW)
                    val searchTerm: String? = message.substringAfter("search")
                    site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                    startActivity(site)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }


    private fun customMessage(message: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}
