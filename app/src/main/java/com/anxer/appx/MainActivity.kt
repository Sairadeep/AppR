package com.anxer.appx

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.buttonSubmit.setOnClickListener {
            val userName = mainBinding.userName.text.toString()
            Log.d("Name", userName)
        }
        mainBinding.layout.setOnClickListener {
            hideKeyBoard(view)
        }
        // R = 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Log.d(
            "OSVersion",
            "OS Version is Higher"
        ) else connection()
    }

    private fun connection() {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                Log.d("serviceConnection", "Service is connected")
                aidlInterface = IvAidlInterface.Stub.asInterface(p1)
                val userName = mainBinding.userName.text.toString()
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

            override fun onServiceDisconnected(p0: ComponentName?) {
                unbindService(this)
                Log.d("Bye", "Bye")
            }
        }
        val intent = Intent("MyPalindromeService")
        intent.setPackage("com.anxer.appx")
        bindService(intent, connection, BIND_AUTO_CREATE)
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

