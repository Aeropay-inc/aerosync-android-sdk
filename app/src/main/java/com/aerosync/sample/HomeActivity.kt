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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        }
    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val config = Widget(this, this);
                config.environment = "DEV"; //DEV, STAGE, PROD
                config.token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4OTFjZDNiYi01YjQ0LTQ5NzAtYTM1Zi0xMGY2ODkyNjFkYjkiLCJleHAiOjE2OTY5MjEzMTcsInVzZXJJZCI6ImUzODg1ZjU0ODllYzRiNTY4MDgyNTBkM2Y4MTBkYmMxIiwidXNlclBhc3N3b3JkIjoiMDQwZmRhNTExZjEzNGE2NDg4OTdiNmFjMmRjODBhMmQiLCJDbGllbnRJZCI6InRlc3QxIiwiQ2xpZW50TmFtZSI6ImNsaWVudDEiLCJzZXNzaW9uSWQiOiIzNzNlNzU5YjQ2OWU0NTQ3YmM2NTE0NzVkZDMyMTU5MyJ9.MwKZXb-Wm4Mtcbc0bXjyBi_rxrKMEz-uyXngZtzj78U";
                config.open();
            }
        }
    }

    override fun onSuccess(response: String?, context: Context) {
        // perform steps when user have completed the bank link workflow
        Toast.makeText(context, "onSuccess--> $response", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent);
    }

    override fun onEvent(type: String?, payload: String?, context: Context) {
        // capture all the Aerosync events
        Toast.makeText(context, "onEvent--> $payload", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String?, context: Context) {
        // error handling
        Toast.makeText(context, "onError--> $error", Toast.LENGTH_SHORT).show()
    }

    override fun onClose(context: Context) {
        // when widget is closed by user
        Toast.makeText(context,"widget closed", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent);
    }
}