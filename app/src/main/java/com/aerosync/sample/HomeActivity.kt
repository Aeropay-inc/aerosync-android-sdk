package com.aerosync.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View;
import com.aerosync.bank_link_sdk.Widget;


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("isTaskRoot", isTaskRoot.toString())
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        if (action != null) {
            Log.d("action--->", action)
        }
        Log.d("data--->", data.toString())
        }
    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                var config = Widget(this);
                config.environment = "DEV"; //DEV, STAGE,PROD
                config.deeplink = "aerosync://bank-link";
                config.token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5Y2QyOGNkNC1iZjI2LTQxYWItODQ0OS00Yzk2NjNiYzhiOTgiLCJleHAiOjE2OTY1MzA5MDgsInVzZXJJZCI6IjMxZDk0NTJmYzY4YzQ1MGVhMTZmMjY4YTAyNGUzMWE0IiwidXNlclBhc3N3b3JkIjoiNWU4ZjNkYWFlZjQwNDI5NzgzMWU2ODRjNTgxNjdiOGMiLCJDbGllbnRJZCI6IjFmMjEyMzU2LWZjZWItNDJlYi1hYmU0LWFhOTIyYWNmMmNkNSIsIkNsaWVudE5hbWUiOiJBZXJvcGF5IERldiIsInNlc3Npb25JZCI6ImJlOGQ3YzYxZjJjNDQ2YzJhNWU5OWEwMzA0YTVlZjIwIn0.60b11g-1eGdOaCFxOtRS2arE2ukEJc4G36V6tR0PziU";
                config.open();
            }
        }
    }
}