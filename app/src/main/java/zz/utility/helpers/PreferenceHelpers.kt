@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

inline fun Context.getDefaultPref(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

// Getters

inline fun Context.prefGet(key: String, def: String): String = { getDefaultPref().getString(key, def) }.or { def }!!

inline fun Context.prefGet(key: String, def: Boolean): Boolean = { getDefaultPref().getBoolean(key, def) }.or { def }

inline fun Context.prefGet(key: String, def: Int): Int = { getDefaultPref().getInt(key, def) }.or { def }

inline fun Context.prefGet(key: String, def: Float): Float = { getDefaultPref().getFloat(key, def) }.or { def }

inline fun Context.prefGet(key: String, def: Long): Long = { getDefaultPref().getLong(key, def) }.or { def }

inline fun Context.prefGetSet(key: String, def: Set<String>): Set<String> = { getDefaultPref().getStringSet(key, def) }.or { def }!!

fun Context.preferences(f: SharedPreferences.Editor.() -> Unit) {
    val d = getDefaultPref().edit()
    d.f()
    d.apply()
}