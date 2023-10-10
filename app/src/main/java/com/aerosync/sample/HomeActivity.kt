package com.aerosync.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.Widget


class HomeActivity : AppCompatActivity(), EventListener {

    private val config = Widget(this, this);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        }
    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                config.environment = "DEV"; //DEV, STAGE, PROD
                config.token = ""; // Add Aerosync token
                config.open();
            }
        }
    }

    override fun onSuccess(response: String?, context: Context) {
        // perform steps when user have completed the bank link workflow
        // sample code
        Toast.makeText(context, "onSuccess--> $response", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent);
    }

    override fun onEvent(type: String?, payload: String?, context: Context) {
        // capture all the Aerosync events
        // sample code
        Toast.makeText(context, "onEvent--> $payload", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String?, context: Context) {
        // error handling
        // sample code
        Toast.makeText(context, "onError--> $error", Toast.LENGTH_SHORT).show()
    }

    override fun onClose(context: Context) {
        // when widget is closed by user
        // sample code
        Toast.makeText(context,"widget closed", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent);
    }
}