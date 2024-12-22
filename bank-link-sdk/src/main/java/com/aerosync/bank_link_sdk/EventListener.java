package com.aerosync.bank_link_sdk;

import android.content.Context;


public interface EventListener {
    void onSuccess(String response, Context context);
    void onEvent(WidgetEventType type, String payload, Context context);
    void onError(String error, Context context);
    void onClose(Context context);
}
