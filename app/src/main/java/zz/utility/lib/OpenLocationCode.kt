package zz.utility.lib

import java.math.BigDecimal
import java.util.*

/**
 * Representation of open location code. https://github.com/google/open-location-code The
 * OpenLocationCode class is a wrapper around String value `code`, which guarantees that the
 * value is a valid Open Location Code.
 *
 * @author Jiri Semecky
 */
class OpenLocationCode {


    /**
     * The state of the OpenLocationCode.
     */
    val code: String?

    /**
     * Returns whether this [OpenLocationCode] is a full Open Location Code.
     */
    val isFull: Boolean
        get() = code!!.indexOf(SEPARATOR) == SEPARATOR_POSITION.toInt()

    /**
     * Returns whether this [OpenLocationCode] is a short Open Location Code.
     */
    val isShort: Boolean
        get() = code!!.indexOf(SEPARATOR) >= 0 && code.indexOf(SEPARATOR) < SEPARATOR_POSITION.toInt()

    /**
     * Returns whether this [OpenLocationCode] is a padded Open Location Code, meaning that it
     * contains less than 8 valid digits.
     */
    private val isPadded: Boolean
        get() = code!!.indexOf(SUFFIX_PADDING) >= 0

    /**
     * Class providing information about area covered by Open Location Code.
     */
    inner class CodeArea(
            private val southLatitude: BigDecimal,
            private val westLongitude: BigDecimal,
            private val northLatitude: BigDecimal,
            private val eastLongitude: BigDecimal) {

        val latitudeHeight: Double
            get() = northLatitude.subtract(southLatitude).toDouble()

        val longitudeWidth: Double
            get() = eastLongitude.subtract(westLongitude).toDouble()

        val centerLatitude: Double
            get() = southLatitude.add(northLatitude).toDouble() / 2

        val centerLongitude: Double
            get() = westLongitude.add(eastLongitude).toDouble() / 2

        fun getSouthLatitude(): Double {
            return southLatitude.toDouble()
        }

        fun getWestLongitude(): Double {
            return westLongitude.toDouble()
        }

        fun getNorthLatitude(): Double {
            return northLatitude.toDouble()
        }

        fun getEastLongitude(): Double {
            return eastLongitude.toDouble()
        }
    }

    /**
     * Creates Open Location Code for the provided code.
     */
    constructor(code: String) {
        if (!isValidCode(code)) {
            throw IllegalArgumentException(
                    "The provided code '$code' is not a valid Open Location Code.")
        }
        this.code = code.toUpperCase()
    }

    /**
     * Creates Open Location Code from the provided latitude, longitude and desired code length.
     */
    @Throws(IllegalArgumentException::class)
    @JvmOverloads constructor(latitude: Double, longitude: Double, codeLength: Int = 10) {
        var latitude = latitude
        var longitude = longitude
        if (codeLength < 4 || (codeLength < 10) and (codeLength % 2 == 1)) {
            throw IllegalArgumentException("Illegal code length " + codeLength)
        }

        latitude = clipLatitude(latitude)
        longitude = normalizeLongitude(longitude)

        // Latitude 90 needs to be adjusted to be just less, so the returned code can also be decoded.
        if (latitude == 90.0) {
            latitude = latitude - 0.9 * computeLatitudePrecision(codeLength)
        }

        val codeBuilder = StringBuilder()

        // Ensure the latitude and longitude are within [0, 180] and [0, 360) respectively.
        /* Note: double type can't be used because of the rounding arithmetic due to floating point
     * implementation. Eg. "8.95 - 8" can give result 0.9499999999999 instead of 0.95 which
     * incorrectly classify the points on the border of a cell.
     */
        var remainingLongitude = BigDecimal(longitude + 180)
        var remainingLatitude = BigDecimal(latitude + 90)

        // Create up to 10 significant digits from pairs alternating latitude and longitude.
        var generatedDigits = 0

        while (generatedDigits < codeLength) {
            // Always the integer part of the remaining latitude/longitude will be used for the following
            // digit.
            if (generatedDigits == 0) {
                // First step World division: Map <0..400) to <0..20) for both latitude and longitude.
                remainingLatitude = remainingLatitude.divide(BD_20)
                remainingLongitude = remainingLongitude.divide(BD_20)
            } else if (generatedDigits < 10) {
                remainingLatitude = remainingLatitude.multiply(BD_20)
                remainingLongitude = remainingLongitude.multiply(BD_20)
            } else {
                remainingLatitude = remainingLatitude.multiply(BD_5)
                remainingLongitude = remainingLongitude.multiply(BD_4)
            }
            val latitudeDigit = remainingLatitude.toInt()
            val longitudeDigit = remainingLongitude.toInt()
            if (generatedDigits < 10) {
                codeBuilder.append(ALPHABET[latitudeDigit])
                codeBuilder.append(ALPHABET[longitudeDigit])
                generatedDigits += 2
            } else {
                codeBuilder.append(ALPHABET[4 * latitudeDigit + longitudeDigit])
                generatedDigits += 1
            }
            remainingLatitude = remainingLatitude.subtract(BigDecimal(latitudeDigit))
            remainingLongitude = remainingLongitude.subtract(BigDecimal(longitudeDigit))
            if (generatedDigits == SEPARATOR_POSITION.toInt()) {
                codeBuilder.append(SEPARATOR)
            }
        }
        if (generatedDigits < SEPARATOR_POSITION.toInt()) {
            while (generatedDigits < SEPARATOR_POSITION.toInt()) {
                codeBuilder.append(SUFFIX_PADDING)
                generatedDigits++
            }
            codeBuilder.append(SEPARATOR)
        }
        this.code = codeBuilder.toString()
    }

    /**
     * Decodes [OpenLocationCode] object into [CodeArea] object encapsulating
     * latitude/longitude bounding box.
     */
    fun decode(): CodeArea {
        if (!isFullCode(code)) {
            throw IllegalStateException(
                    "Method decode() could only be called on valid full codes, code was $code.")
        }
        val decoded = code!!.replace("[0+]".toRegex(), "")
        // Decode the lat/lng pair component.
        var southLatitude = BD_0
        var westLongitude = BD_0

        var digit = 0
        var latitudeResolution = 400.0
        var longitudeResolution = 400.0

        // Decode pair.
        while (digit < decoded.length) {
            if (digit < 10) {
                latitudeResolution /= 20.0
                longitudeResolution /= 20.0
                southLatitude = southLatitude.add(
                        BigDecimal(latitudeResolution * CHARACTER_TO_INDEX[decoded[digit]]!!))
                westLongitude = westLongitude.add(
                        BigDecimal(
                                longitudeResolution * CHARACTER_TO_INDEX[decoded[digit + 1]]!!))
                digit += 2
            } else {
                latitudeResolution /= 5.0
                longitudeResolution /= 4.0
                southLatitude = southLatitude.add(
                        BigDecimal(
                                latitudeResolution * (CHARACTER_TO_INDEX[decoded[digit]]!! / 4)))
                westLongitude = westLongitude.add(
                        BigDecimal(
                                longitudeResolution * (CHARACTER_TO_INDEX[decoded[digit]]!! % 4)))
                digit += 1
            }
        }
        return CodeArea(
                southLatitude.subtract(BD_90),
                westLongitude.subtract(BD_180),
                southLatitude.subtract(BD_90).add(BigDecimal(latitudeResolution)),
                westLongitude.subtract(BD_180).add(BigDecimal(longitudeResolution)))
    }

    /**
     * Returns short [OpenLocationCode] from the full Open Location Code created by removing
     * four or six digits, depending on the provided reference point.  It removes as many digits as
     * possible.
     */
    fun shorten(referenceLatitude: Double, referenceLongitude: Double): OpenLocationCode {
        if (!isFull) {
            throw IllegalStateException("shorten() method could only be called on a full code.")
        }
        if (isPadded) {
            throw IllegalStateException("shorten() method can not be called on a padded code.")
        }

        val codeArea = decode()
        val latitudeDiff = Math.abs(referenceLatitude - codeArea.centerLatitude)
        val longitudeDiff = Math.abs(referenceLongitude - codeArea.centerLongitude)

        if (latitudeDiff < LATITUDE_PRECISION_8_DIGITS && longitudeDiff < LATITUDE_PRECISION_8_DIGITS) {
            return OpenLocationCode(code!!.substring(8))
        }
        if (latitudeDiff < LATITUDE_PRECISION_6_DIGITS && longitudeDiff < LATITUDE_PRECISION_6_DIGITS) {
            return OpenLocationCode(code!!.substring(6))
        }
        if (latitudeDiff < LATITUDE_PRECISION_4_DIGITS && longitudeDiff < LATITUDE_PRECISION_4_DIGITS) {
            return OpenLocationCode(code!!.substring(4))
        }
        throw IllegalArgumentException(
                "Reference location is too far from the Open Location Code center.")
    }

    /**
     * Returns an [OpenLocationCode] object representing a full Open Location Code from this
     * (short) Open Location Code, given the reference location.
     */
    fun recover(referenceLatitude: Double, referenceLongitude: Double): OpenLocationCode {
        var referenceLatitude = referenceLatitude
        var referenceLongitude = referenceLongitude
        if (isFull) {
            // Note: each code is either full xor short, no other option.
            return this
        }
        referenceLatitude = clipLatitude(referenceLatitude)
        referenceLongitude = normalizeLongitude(referenceLongitude)

        val digitsToRecover = 8 - code!!.indexOf(SEPARATOR)
        // The resolution (height and width) of the padded area in degrees.
        val paddedAreaSize = Math.pow(20.0, (2 - digitsToRecover / 2).toDouble())

        // Use the reference location to pad the supplied short code and decode it.
        val recoveredPrefix = OpenLocationCode(referenceLatitude, referenceLongitude)
                .code!!
                .substring(0, digitsToRecover)
        val recovered = OpenLocationCode(recoveredPrefix + code!!)
        val recoveredCodeArea = recovered.decode()
        var recoveredLatitude = recoveredCodeArea.centerLatitude
        var recoveredLongitude = recoveredCodeArea.centerLongitude

        // Move the recovered latitude by one resolution up or down if it is too far from the reference.
        val latitudeDiff = recoveredLatitude - referenceLatitude
        if (latitudeDiff > paddedAreaSize / 2) {
            recoveredLatitude -= paddedAreaSize
        } else if (latitudeDiff < -paddedAreaSize / 2) {
            recoveredLatitude += paddedAreaSize
        }

        // Move the recovered longitude by one resolution up or down if it is too far from the
        // reference.
        val longitudeDiff = recoveredCodeArea.centerLongitude - referenceLongitude
        if (longitudeDiff > paddedAreaSize / 2) {
            recoveredLongitude -= paddedAreaSize
        } else if (longitudeDiff < -paddedAreaSize / 2) {
            recoveredLongitude += paddedAreaSize
        }

        return OpenLocationCode(
                recoveredLatitude, recoveredLongitude, recovered.code!!.length - 1)
    }

    /**
     * Returns whether the bounding box specified by the Open Location Code contains provided point.
     */
    fun contains(latitude: Double, longitude: Double): Boolean {
        val codeArea = decode()
        return (codeArea.getSouthLatitude() <= latitude
                && latitude < codeArea.getNorthLatitude()
                && codeArea.getWestLongitude() <= longitude
                && longitude < codeArea.getEastLongitude())
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as OpenLocationCode?
        return hashCode() == that!!.hashCode()
    }

    override fun hashCode(): Int {
        return code?.hashCode() ?: 0
    }

    override fun toString(): String = code!!

    companion object {

        private val BD_0 = BigDecimal(0)
        private val BD_5 = BigDecimal(5)
        private val BD_4 = BigDecimal(4)
        private val BD_20 = BigDecimal(20)
        private val BD_90 = BigDecimal(90)
        private val BD_180 = BigDecimal(180)
        private val LATITUDE_PRECISION_8_DIGITS = computeLatitudePrecision(8) / 4
        private val LATITUDE_PRECISION_6_DIGITS = computeLatitudePrecision(6) / 4
        private val LATITUDE_PRECISION_4_DIGITS = computeLatitudePrecision(4) / 4

        private val ALPHABET = "23456789CFGHJMPQRVWX".toCharArray()
        private val CHARACTER_TO_INDEX = HashMap<Char, Int>()

        init {
            var index = 0
            for (character in ALPHABET) {
                val lowerCaseCharacter = Character.toLowerCase(character)
                CHARACTER_TO_INDEX.put(character, index)
                CHARACTER_TO_INDEX.put(lowerCaseCharacter, index)
                index++
            }
        }

        private val SEPARATOR = '+'
        private val SEPARATOR_POSITION: Char = 8.toChar()
        private val SUFFIX_PADDING = '0'

        /**
         * Encodes latitude/longitude into 10 digit Open Location Code. This method is equivalent to
         * creating the OpenLocationCode object and getting the code from it.
         */
        fun encode(latitude: Double, longitude: Double): String? {
            return OpenLocationCode(latitude, longitude).code
        }

        /**
         * Encodes latitude/longitude into Open Location Code of the provided length. This method is
         * equivalent to creating the OpenLocationCode object and getting the code from it.
         */
        fun encode(latitude: Double, longitude: Double, codeLength: Int): String? {
            return OpenLocationCode(latitude, longitude, codeLength).code
        }

        /**
         * Decodes code representing Open Location Code into [CodeArea] object encapsulating
         * latitude/longitude bounding box.
         *
         * @param code Open Location Code to be decoded.
         * @throws IllegalArgumentException if the provided code is not a valid Open Location Code.
         */
        @Throws(IllegalArgumentException::class)
        fun decode(code: String): CodeArea {
            return OpenLocationCode(code).decode()
        }

        /**
         * Returns whether the provided Open Location Code is a full Open Location Code.
         */
        @Throws(IllegalArgumentException::class)
        fun isFull(code: String): Boolean {
            return OpenLocationCode(code).isFull
        }

        /**
         * Returns whether the provided Open Location Code is a short Open Location Code.
         */
        @Throws(IllegalArgumentException::class)
        fun isShort(code: String): Boolean {
            return OpenLocationCode(code).isShort
        }

        /**
         * Returns whether the provided Open Location Code is a padded Open Location Code, meaning that it
         * contains less than 8 valid digits.
         */
        @Throws(IllegalArgumentException::class)
        fun isPadded(code: String): Boolean {
            return OpenLocationCode(code).isPadded
        }

        // Exposed static helper methods.

        /**
         * Returns whether the provided string is a valid Open Location code.
         */
        fun isValidCode(code: String?): Boolean {
            if (code == null || code.length < 2) {
                return false
            }

            // There must be exactly one separator.
            val separatorPosition = code.indexOf(SEPARATOR)
            if (separatorPosition == -1) {
                return false
            }
            if (separatorPosition != code.lastIndexOf(SEPARATOR)) {
                return false
            }

            if (separatorPosition % 2 != 0) {
                return false
            }

            // Check first two characters: only some values from the alphabet are permitted.
            if (separatorPosition == 8) {
                // First latitude character can only have first 9 values.
                val index0 = CHARACTER_TO_INDEX[code[0]]
                if (index0 == null || index0 > 8) {
                    return false
                }

                // First longitude character can only have first 18 values.
                val index1 = CHARACTER_TO_INDEX[code[1]]
                if (index1 == null || index1 > 17) {
                    return false
                }
            }

            // Check the characters before the separator.
            var paddingStarted = false
            for (i in 0 until separatorPosition) {
                if (paddingStarted) {
                    // Once padding starts, there must not be anything but padding.
                    if (code[i] != SUFFIX_PADDING) {
                        return false
                    }
                    continue
                }
                if (CHARACTER_TO_INDEX.keys.contains(code[i])) {
                    continue
                }
                if (SUFFIX_PADDING == code[i]) {
                    paddingStarted = true
                    // Padding can start on even character: 2, 4 or 6.
                    if (i != 2 && i != 4 && i != 6) {
                        return false
                    }
                    continue
                }
                return false  // Illegal character.
            }

            // Check the characters after the separator.
            if (code.length > separatorPosition + 1) {
                if (paddingStarted) {
                    return false
                }
                // Only one character after separator is forbidden.
                if (code.length == separatorPosition + 2) {
                    return false
                }
                for (i in separatorPosition + 1 until code.length) {
                    if (!CHARACTER_TO_INDEX.keys.contains(code[i])) {
                        return false
                    }
                }
            }

            return true
        }

        /**
         * Returns if the code is a valid full Open Location Code.
         */
        fun isFullCode(code: String?): Boolean {
            try {
                return OpenLocationCode(code!!).isFull
            } catch (e: IllegalArgumentException) {
                return false
            }

        }

        /**
         * Returns if the code is a valid short Open Location Code.
         */
        fun isShortCode(code: String): Boolean {
            try {
                return OpenLocationCode(code).isShort
            } catch (e: IllegalArgumentException) {
                return false
            }

        }

        // Private static methods.

        private fun clipLatitude(latitude: Double): Double {
            return Math.min(Math.max(latitude, -90.0), 90.0)
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

        /**
         * Compute the latitude precision value for a given code length. Lengths <= 10 have the same
         * precision for latitude and longitude, but lengths > 10 have different precisions due to the
         * grid method having fewer columns than rows. Copied from the JS implementation.
         */
        private fun computeLatitudePrecision(codeLength: Int): Double {
            return if (codeLength <= 10) {
                Math.pow(20.0, Math.floor((codeLength / -2 + 2).toDouble()))
            } else Math.pow(20.0, -3.0) / Math.pow(5.0, (codeLength - 10).toDouble())
        }
    }
}
/**
 * Creates Open Location Code with code length 10 from the provided latitude, longitude.
 */