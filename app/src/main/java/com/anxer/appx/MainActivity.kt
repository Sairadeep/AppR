package com.anxer.appx

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anxer.appr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private var aidlInterface: IvAidlInterface? = null
    private var isServiceConnected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        mainBinding.buttonSubmit.setOnClickListener {
            val userName = mainBinding.userName.text.toString()
            Log.d("Name", userName)
            connection()
        }
    }

    private fun connection() {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                Log.d("serviceConnection", "Service is connected")
                aidlInterface = IvAidlInterface.Stub.asInterface(p1)
                val userName = mainBinding.userName.text.toString()
                //Log.d("Reverse","userName:  $userName")
                val reverseName: String? = aidlInterface?.getUseremailName(userName.toString())
                Log.d("Reverse", reverseName.toString())
                Toast.makeText(this@MainActivity, "Palindrome Value: $reverseName", Toast.LENGTH_SHORT).show()
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
}

