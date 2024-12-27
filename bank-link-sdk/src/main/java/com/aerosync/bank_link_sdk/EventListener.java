package com.aerosync.bank_link_sdk;

import android.content.Context;


public interface EventListener {
    void onSuccess(PayloadSuccessType event, Context context);
    void onEvent(PayloadEventType event, Context context);
    void onError(String error, Context context);
    void onClose(Context context);
}
