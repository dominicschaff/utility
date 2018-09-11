@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.io.*

inline fun String.fileExists(): Boolean = File(this).exists()
inline fun String.fileAsJsonArray(): JsonArray = JsonParser().parse(BufferedReader(FileReader(this))).asJsonArray
inline fun File.asJsonArray(): JsonArray = JsonParser().parse(BufferedReader(FileReader(this))).asJsonArray
inline fun String.fileAsJsonObject(): JsonObject = JsonParser().parse(BufferedReader(FileReader(this))).asJsonObject
inline fun File.asJsonObject(): JsonObject = JsonParser().parse(BufferedReader(FileReader(this))).asJsonObject

inline fun JsonObject.run(f: JsonObject.() -> Unit): JsonObject {
    f()
    return this
}

inline fun JsonArray.run(f: JsonArray.() -> Unit): JsonArray {
    this.f()
    return this
}

fun JsonObject.add(key: String, value: Int): JsonObject = run { addProperty(key, value) }

fun JsonObject.add(key: String, value: String): JsonObject = run { addProperty(key, value) }

fun JsonObject.add(key: String, value: Double): JsonObject = run { addProperty(key, value) }
fun JsonObject.add(key: String, value: Float): JsonObject = run { addProperty(key, value) }

fun JsonObject.add(key: String, value: Boolean): JsonObject = run { addProperty(key, value) }

fun JsonObject.add(key: String, value: Long): JsonObject = run { addProperty(key, value) }

fun JsonObject.s(key: String, defaultValue: String = ""): String =
        { if (has(key)) get(key).asString else defaultValue }.or { defaultValue }

fun JsonObject.l(key: String, defaultValue: Long = 0): Long =
        { if (has(key)) get(key).asLong else defaultValue }.or { defaultValue }

fun JsonObject.i(key: String, defaultValue: Int = 0): Int =
        { if (has(key)) get(key).asInt else defaultValue }.or { defaultValue }

fun JsonObject.d(key: String, defaultValue: Double = 0.0): Double =
        { if (has(key)) get(key).asDouble else defaultValue }.or { defaultValue }

fun JsonObject.b(key: String, defaultValue: Boolean = false): Boolean =
        { if (has(key)) get(key).asBoolean else defaultValue }.or { defaultValue }

fun JsonObject.a(key: String, defaultValue: JsonArray = JsonArray()): JsonArray =
        { if (has(key)) get(key).asJsonArray else defaultValue }.or { defaultValue }

fun JsonObject.o(key: String, defaultValue: JsonObject = JsonObject()): JsonObject =
        { if (has(key)) get(key).asJsonObject else defaultValue }.or { defaultValue }

fun JsonArray.append(value: String): JsonArray = run { add(JsonPrimitive(value)) }
fun JsonArray.append(value: Long): JsonArray = run { add(JsonPrimitive(value)) }
fun JsonArray.append(value: Int): JsonArray = run { add(JsonPrimitive(value)) }
fun JsonArray.append(value: Double): JsonArray = run { add(JsonPrimitive(value)) }
fun JsonArray.append(value: Boolean): JsonArray = run { add(JsonPrimitive(value)) }
fun JsonArray.append(value: JsonArray): JsonArray = run { add(value) }
fun JsonArray.append(value: JsonObject): JsonArray = run { add(value) }

fun JsonArray.d(position: Int): Double = get(position).asDouble

fun JsonArray.i(position: Int): Int = get(position).asInt

fun JsonArray.b(position: Int): Boolean = get(position).asBoolean

fun JsonArray.s(position: Int): String = get(position).asString

fun JsonArray.o(position: Int): JsonObject = get(position).asJsonObject

fun JsonArray.isJsonObject(index: Int): Boolean = get(index).isJsonObject

inline fun <T> JsonArray.mapObject(f: JsonObject.() -> T): List<T> = this.map { it.asJsonObject.f() }


fun JsonObject.appendToFile(file: File) {
    var stream: FileOutputStream? = null
    try {
        stream = FileOutputStream(file, true)
        stream.write(this.toString().toByteArray())
        stream.write("\n".toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        { stream?.close() }.orPrint()
    }
}