package com.aerosync.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.PayloadEventType
import com.aerosync.bank_link_sdk.PayloadSuccessType
import com.aerosync.bank_link_sdk.Widget

class HomeActivity : FragmentActivity(), EventListener {

    private val config = Widget(this, this);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        }
    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                config.environment = "PROD"; // STAGE, PROD
                config.token = ""; // Add Aerosync token
                config.handleMFA = false;
                config.userId = "";
                config.jobId = "";
                config.open();
            }
        }
    }

    override fun onSuccess(event: PayloadSuccessType?, context: Context?) {
        // perform steps when user have completed the bank link workflow
        // sample code
        if (event != null) {
            Toast.makeText(context,  "user = ${event.user_id}, " +
                    "ClientName = ${event.ClientName}, " +
                    "FILoginAcctId = ${event.FILoginAcctId}", Toast.LENGTH_SHORT).show()

        };
        val intent = Intent(context, HomeActivity::class.java)
        context?.startActivity(intent);
    }

    override fun onEvent(event: PayloadEventType?, context: Context?) {
        // capture all the Aerosync events
        // sample code
        if (event != null) {
            Toast.makeText(context, "ONEVENT: onLoadApi = ${event.onLoadApi},\" +\n" +
                    "                    \"pageTitle = ${event.pageTitle}", Toast.LENGTH_SHORT).show()

        };
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