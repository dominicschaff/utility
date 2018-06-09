@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

inline fun Context.getDefaultPref(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

// Getters

inline fun Context.prefGet(key: String, def: String): String = tryCatch({ getDefaultPref().getString(key, def) }, def)

inline fun Context.prefGet(key: String, def: Boolean): Boolean = tryCatch({ getDefaultPref().getBoolean(key, def) }, def)

inline fun Context.prefGet(key: String, def: Int): Int = tryCatch({ getDefaultPref().getInt(key, def) }, def)

inline fun Context.prefGet(key: String, def: Float): Float = tryCatch({ getDefaultPref().getFloat(key, def) }, def)

inline fun Context.prefGet(key: String, def: Long): Long = tryCatch({ getDefaultPref().getLong(key, def) }, def)

inline fun Context.prefGetSet(key: String, def: Set<String>): Set<String> = tryCatch({ getDefaultPref().getStringSet(key, def) }, def)

// Setters

inline fun Context.prefSet(key: String, value: String) = getDefaultPref().edit().putString(key, value).apply()

inline fun Context.prefSet(key: String, value: Boolean) = getDefaultPref().edit().putBoolean(key, value).apply()

inline fun Context.prefSet(key: String, value: Int) = getDefaultPref().edit().putInt(key, value).apply()

inline fun Context.prefSet(key: String, value: Float) = getDefaultPref().edit().putFloat(key, value).apply()

inline fun Context.prefSet(key: String, value: Long) = getDefaultPref().edit().putLong(key, value).apply()

inline fun Context.prefSet(key: String, value: Set<String>) = getDefaultPref().edit().putStringSet(key, value).apply()

// Deleter

inline fun Context.prefDel(key: String) = getDefaultPref().edit().remove(key).apply()