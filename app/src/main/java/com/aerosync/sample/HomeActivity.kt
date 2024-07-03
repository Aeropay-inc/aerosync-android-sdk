package com.aerosync.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.Widget


class HomeActivity : FragmentActivity(), EventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val dropdown = findViewById<Spinner>(R.id.spinner)
        //create a list of items for the spinner.
        val items = arrayOf("DEV", "STAGE", "PROD")
        val adapter: Any? = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter as SpinnerAdapter?
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val token = findViewById<EditText>(R.id.token).text;
                val configurationId = findViewById<EditText>(R.id.configurationId).text;
                val aeroPassUserUuid = findViewById<EditText>(R.id.aeroPassUserUuid).text;
                val envSpinner = findViewById<View>(R.id.spinner) as Spinner
                val environment = envSpinner.selectedItem
                val config = Widget(this, this);
                config.environment = environment.toString(); //DEV, STAGE,PROD
                config.token = token.toString();
                config.configurationId = configurationId.toString();
                config.aeroPassUserUuid = aeroPassUserUuid.toString();
                Log.d("widget", token.toString())
                Log.d("widget", configurationId.toString())
                Log.d("widget", environment.toString())
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
        val output = findViewById<TextView>(R.id.output);
        output.text = response;

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
        (context as Activity).finish()
    }
}