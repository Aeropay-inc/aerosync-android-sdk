package com.aerosync.bank_link_sdk;

import androidx.annotation.NonNull;

/**
 * Receives the Aerosync widget events.
 *
 * onSuccess and onClose are called after the widget has closed, on the screen
 * that created the {@link Widget}. onEvent and onError are called while the
 * widget is open. All callbacks run on the main thread.
 */
public interface EventListener {
    void onSuccess(@NonNull PayloadSuccessType event);
    void onEvent(@NonNull PayloadEventType event);
    void onError(@NonNull String error);
    void onClose();
}
