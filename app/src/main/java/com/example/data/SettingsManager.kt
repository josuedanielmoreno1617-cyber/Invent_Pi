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

    var businessData: String
        get() = prefs.getString("business_data", "") ?: ""
        set(value) = prefs.edit().putString("business_data", value).apply()

    var companyName: String
        get() = prefs.getString("company_name", "") ?: ""
        set(value) = prefs.edit().putString("company_name", value).apply()

    var rucNumber: String
        get() = prefs.getString("ruc_number", "") ?: ""
        set(value) = prefs.edit().putString("ruc_number", value).apply()

    var invoiceCount: Int
        get() = prefs.getInt("invoice_count", 1)
        set(value) = prefs.edit().putInt("invoice_count", value).apply()

    var storeLocation: String
        get() = prefs.getString("store_location", "") ?: ""
        set(value) = prefs.edit().putString("store_location", value).apply()

    var notifyPriceChanges: Boolean
        get() = prefs.getBoolean("notify_price_changes", true)
        set(value) = prefs.edit().putBoolean("notify_price_changes", value).apply()

    var enableProductImage: Boolean
        get() = prefs.getBoolean("enable_product_image", true)
        set(value) = prefs.edit().putBoolean("enable_product_image", value).apply()

    var hasSeenTutorial: Boolean
        get() = prefs.getBoolean("has_seen_tutorial", false)
        set(value) = prefs.edit().putBoolean("has_seen_tutorial", value).apply()

    var currencySymbol: String
        get() = prefs.getString("currency_symbol", "$") ?: "$"
        set(value) = prefs.edit().putString("currency_symbol", value).apply()
}
