package com.anxer.appx

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anxer.appr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private var aidlInterface: IvAidlInterface? = null
    private var isServiceRunning: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.buttonSubmit.setOnClickListener {
//            val userName =
            getReversedString(mainBinding.userName.text.toString())
//            Log.d("Name", userName)
        }
        mainBinding.layout.setOnClickListener {
            hideKeyBoard(view)
        }
    }

    private fun getReversedString(userName: String) {
        if (aidlInterface == null) {
            val intent = Intent("MyPalindromeService")
            intent.setPackage("com.anxer.appx")
            // If service started then only bind it.
            isServiceRunning = Utils.isServiceRunning(this@MainActivity, intent::class.java)
            if (!isServiceRunning) {
                Log.d("serviceStatus", "Service is starting now and connected.")
                startService(intent)
                bindService(intent, connection, BIND_AUTO_CREATE)
            } else {
                Log.d("serviceStatus", "Service is already started and also connected.")
            }
            return
        }
        val reverseName: String? = aidlInterface?.getUseremailName(userName)
        Log.d("Reverse", reverseName.toString())
        Toast.makeText(
            this@MainActivity,
            "Palindrome Value: $reverseName",
            Toast.LENGTH_SHORT
        ).show()
        if (userName.equals(reverseName, ignoreCase = true)) Toast.makeText(
            this@MainActivity,
            "$userName is a palindrome.",
            Toast.LENGTH_LONG
        ).show() else Toast.makeText(
            this@MainActivity,
            "$userName is a not a palindrome.",
            Toast.LENGTH_LONG
        ).show()
    }


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("serviceConnection", "Service is connected")
            aidlInterface = IvAidlInterface.Stub.asInterface(p1)
            getReversedString(mainBinding.userName.text.toString())
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("Bye", "Bye")
            unbindService(this)
        }
    }


    private fun hideKeyBoard(view: View) {
        val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            // view.windowToken -> The token of the window or view that currently has focus.
            // Passing 0 or InputMethodManager.HIDE_NOT_ALWAYS indicates that the keyboard should be hidden if it is currently visible
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

