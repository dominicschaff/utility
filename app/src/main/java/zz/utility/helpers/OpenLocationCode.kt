package zz.utility.helpers

import java.math.BigDecimal
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Representation of open location code. https://github.com/google/open-location-code The
 * OpenLocationCode class is a wrapper around String value `code`, which guarantees that the
 * value is a valid Open Location Code.
 *
 * @author Jiri Semecky
 */
class OpenLocationCode
constructor(latitude: Double, longitude: Double) {


    /**
     * The state of the OpenLocationCode.
     */
    val code: String?

    init {
        var latitude = latitude
        var longitude = longitude
        val codeLength = 10
        latitude = clipLatitude(latitude)
        longitude = normalizeLongitude(longitude)
        if (latitude == 90.0) {
            latitude -= 0.9 * (20.0.pow(floor((10 / -2 + 2).toDouble())))
        }
        val codeBuilder = StringBuilder()
        var remainingLongitude = BigDecimal(longitude + 180)
        var remainingLatitude = BigDecimal(latitude + 90)
        var generatedDigits = 0
        while (generatedDigits < codeLength) {
            // Always the integer part of the remaining latitude/longitude will be used for the following
            // digit.
            when {
                generatedDigits == 0 -> {
                    // First step World division: Map <0..400) to <0..20) for both latitude and longitude.
                    remainingLatitude = remainingLatitude.divide(BD_20)
                    remainingLongitude = remainingLongitude.divide(BD_20)
                }
                generatedDigits < 10 -> {
                    remainingLatitude = remainingLatitude.multiply(BD_20)
                    remainingLongitude = remainingLongitude.multiply(BD_20)
                }
                else -> {
                    remainingLatitude = remainingLatitude.multiply(BD_5)
                    remainingLongitude = remainingLongitude.multiply(BD_4)
                }
            }
            val latitudeDigit = remainingLatitude.toInt()
            val longitudeDigit = remainingLongitude.toInt()
            generatedDigits += if (generatedDigits < 10) {
                codeBuilder.append(ALPHABET[latitudeDigit])
                codeBuilder.append(ALPHABET[longitudeDigit])
                2
            } else {
                codeBuilder.append(ALPHABET[4 * latitudeDigit + longitudeDigit])
                1
            }
            remainingLatitude = remainingLatitude.subtract(BigDecimal(latitudeDigit))
            remainingLongitude = remainingLongitude.subtract(BigDecimal(longitudeDigit))
            if (generatedDigits == SEPARATOR_POSITION.code) {
                codeBuilder.append(SEPARATOR)
            }
        }
        if (generatedDigits < SEPARATOR_POSITION.code) {
            while (generatedDigits < SEPARATOR_POSITION.code) {
                codeBuilder.append(SUFFIX_PADDING)
                generatedDigits++
            }
            codeBuilder.append(SEPARATOR)
        }
        this.code = codeBuilder.toString()
    }

    companion object {

        private val BD_5 = BigDecimal(5)
        private val BD_4 = BigDecimal(4)
        private val BD_20 = BigDecimal(20)

        private val ALPHABET = "23456789CFGHJMPQRVWX".toCharArray()
        private val CHARACTER_TO_INDEX = HashMap<Char, Int>()

        init {
            for ((index, character) in ALPHABET.withIndex()) {
                val lowerCaseCharacter = Character.toLowerCase(character)
                CHARACTER_TO_INDEX[character] = index
                CHARACTER_TO_INDEX[lowerCaseCharacter] = index
            }
        }

        private const val SEPARATOR = '+'
        private const val SEPARATOR_POSITION: Char = 8.toChar()
        private const val SUFFIX_PADDING = '0'

        private fun clipLatitude(latitude: Double): Double {
            return min(max(latitude, -90.0), 90.0)
        }

        private fun normalizeLongitude(longitude: Double): Double {
            var longitude = longitude
            if (longitude < -180) {
                longitude = longitude % 360 + 360
            }
            if (longitude >= 180) {
                longitude = longitude % 360 - 360
            }
            return longitude
        }
    }
}