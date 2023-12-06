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
import com.anxer.appo.IpAidlInterface
import com.anxer.appr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private var aidlInterface: IvAidlInterface? = null
    private var secondaryAidlInterface: IpAidlInterface? = null
    private var isServiceRunning: Boolean = false
    private var isSecondaryServiceRunning: Boolean = false
    private var userName: String = "Loading...!"
    private var reverseName: String = "Empty"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.buttonSubmit.setOnClickListener {
            getReversedString(mainBinding.userName.text.toString())
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
        reverseName = aidlInterface?.getUseremailName(userName).toString()
        Log.d("Reverse", reverseName)
        // App O binding
        if (secondaryAidlInterface == null) {
            val intents = Intent("MyCheckService")
            intents.setPackage("com.anxer.appo")
            isSecondaryServiceRunning =
                Utils.isServiceRunning(this@MainActivity, intents::class.java)
            if (!isSecondaryServiceRunning) {
                Log.d("secondaryServiceStatus", "Secondary service is starting now..!")
                startService(intents)
                bindService(intents, secondaryConnection, BIND_AUTO_CREATE)
            }
        } else {
            val checkValue: Boolean? = secondaryAidlInterface?.checkResult(
                userName,
                aidlInterface?.getUseremailName(userName).toString()
            )
//            if (checkValue != null) Toast.makeText(
//                this@MainActivity,
//                checkValue.toString(),
//                Toast.LENGTH_LONG
//            ).show() else Toast.makeText(
//                this@MainActivity,
//                "checkValue is empty",
//                Toast.LENGTH_LONG
//            ).show()
            if (checkValue == true) Toast.makeText(
                this@MainActivity,
                "$userName is a palindrome",
                Toast.LENGTH_LONG
            ).show() else Toast.makeText(
                this@MainActivity,
                "$userName is not a palindrome",
                Toast.LENGTH_LONG
            ).show()
        }
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

    private val secondaryConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("serviceConnection", "Secondary service is connected")
            secondaryAidlInterface = IpAidlInterface.Stub.asInterface(p1)
            userName = mainBinding.userName.text.toString()
            getReversedString(userName)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("Bye", "Secondary service connection is disconnected")
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

