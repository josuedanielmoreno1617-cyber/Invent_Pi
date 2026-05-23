package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("inventory_settings", Context.MODE_PRIVATE)

    var notifyLowStock: Boolean
        get() = prefs.getBoolean("notify_low_stock", true)
        set(value) = prefs.edit().putBoolean("notify_low_stock", value).apply()

    var notifyPredictions: Boolean
        get() = prefs.getBoolean("notify_predictions", true)
        set(value) = prefs.edit().putBoolean("notify_predictions", value).apply()

    var notifyInventoryChanges: Boolean
        get() = prefs.getBoolean("notify_inventory_changes", true)
        set(value) = prefs.edit().putBoolean("notify_inventory_changes", value).apply()
}
