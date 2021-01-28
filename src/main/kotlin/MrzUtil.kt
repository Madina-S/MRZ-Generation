import java.text.Normalizer

object MRZGenerator {
    private val MRZ_WEIGHTS = intArrayOf(7, 3, 1)
    private const val FILLER = '<'

    fun encode(
        documentType: String,
        surname: String,
        givenNames: String,
        issuingCountry: String,
        nationality: String,
        documentNumber: String,
        sex: String,
        dateOfBirth: MrzDate,
        expirationDate: MrzDate,
        optional: String? = null,
        optional2: String? = null
    ): String {
        val sb = StringBuilder()
        sb.append(documentType)
        if (documentType.length < 2)
            sb.append("<")
        sb.append(toMrz(issuingCountry, 3))

        val documentNumberMRZ = toMrz(documentNumber, 9)
        val docNumberAndOptionalMRZ: String =
            documentNumberMRZ + computeCheckDigitChar(documentNumberMRZ) + toMrz(
                optional,
                15
            )
        sb.append(docNumberAndOptionalMRZ)
        sb.append('\n')

        val dateOfBirthMRZ: String = dateOfBirth.toMrz() + computeCheckDigitChar(
            dateOfBirth.toMrz()
        )
        sb.append(dateOfBirthMRZ)
        sb.append(sex)
        val expiryDateMRZ: String = expirationDate.toMrz() + computeCheckDigitChar(
            expirationDate.toMrz()
        )
        sb.append(expiryDateMRZ)
        sb.append(toMrz(nationality, 3))
        sb.append(toMrz(optional2, 11))
        sb.append(
            computeCheckDigitChar(
                docNumberAndOptionalMRZ +
                        dateOfBirthMRZ +
                        expiryDateMRZ +
                        toMrz(optional2, 11)
            )
        )
        sb.append('\n')

        sb.append(nameToMrz(surname, givenNames))
        sb.append('\n')
        return sb.toString()
    }


    private fun toMrz(string: String?, length: Int): String {
        var text = string ?: ""
        text = text.replace("’", "")
        text = text.replace("'", "")
        text = normalize(text)
        if (length >= 0 && text.length > length) {
            text = text.substring(0, length)
        }
        val sb = StringBuilder(text)
        for (i in sb.indices) {
            if (!isValid(sb[i])) {
                sb.setCharAt(i, FILLER)
            }
        }
        while (sb.length < length) {
            sb.append(FILLER)
        }
        return sb.toString()
    }

    private fun isValid(c: Char): Boolean {
        return c == FILLER || c in '0'..'9' || c in 'A'..'Z'
    }

    private fun nameToMrz(surname: String, givenNames: String): String {
        val length = 30
        require(!isBlank(surname)) { "Parameter surname invalid: $surname" }
        require(!isBlank(givenNames)) { "Parameter givenNames invalid: $givenNames" }
        val surnames = surname.replace(", ", " ").trim { it <= ' ' }.split("[ \n\t\r]+".toRegex()).toTypedArray()
        val given = givenNames.replace(", ", " ").trim { it <= ' ' }.split("[ \n\t\r]+".toRegex()).toTypedArray()
        for (i in surnames.indices) {
            surnames[i] = toMrz(surnames[i], -1)
        }
        for (i in given.indices) {
            given[i] = toMrz(given[i], -1)
        }
        var nameSize: Int = getNameSize(surnames, given)
        var currentlyTruncating = given
        var currentlyTruncatingIndex = given.size - 1
        while (nameSize > length) {
            val ct = currentlyTruncating[currentlyTruncatingIndex]
            val ctsize = ct.length
            if (nameSize - ctsize + 1 <= length) {
                currentlyTruncating[currentlyTruncatingIndex] = ct.substring(0, ctsize - (nameSize - length))
            } else {
                currentlyTruncating[currentlyTruncatingIndex] = ct.substring(0, 1)
                currentlyTruncatingIndex--
                if (currentlyTruncatingIndex < 0) {
                    require(!currentlyTruncating.contentEquals(surnames)) {
                        "Cannot truncate name $surname $givenNames: length too small: $length; truncated to " +
                                toName(surnames, given)
                    }
                    currentlyTruncating = surnames
                    currentlyTruncatingIndex = currentlyTruncating.size - 1
                }
            }
            nameSize = getNameSize(surnames, given)
        }
        return toMrz(
            toName(
                surnames,
                given
            ), length
        )
    }

    private fun toName(surnames: Array<String>, given: Array<String>): String {
        val sb = StringBuilder()
        var first = true
        for (s in surnames) {
            if (first) {
                first = false
            } else {
                sb.append(FILLER)
            }
            sb.append(s)
        }
        sb.append(FILLER)
        for (s in given) {
            sb.append(FILLER)
            sb.append(s)
        }
        return sb.toString()
    }

    private fun isBlank(str: String?) = str == null || str.trim { it <= ' ' }.isEmpty()

    private fun getNameSize(surnames: Array<String>, given: Array<String>): Int {
        var result = 0
        for (s in surnames) {
            result += s.length + 1
        }
        for (s in given) {
            result += s.length + 1
        }
        return result
    }

    private fun getCharacterValue(c: Char): Int {
        if (c == FILLER) {
            return 0
        }
        if (c in '0'..'9') {
            return c - '0'
        }
        if (c in 'A'..'Z') {
            return c - 'A' + 10
        }
        throw RuntimeException("Invalid character in MRZ record: $c")
    }

    private fun computeCheckDigitChar(str: String): Char {
        var result = 0
        for (i in str.indices) {
            result += getCharacterValue(str[i]) * MRZ_WEIGHTS[i % MRZ_WEIGHTS.size]
        }
        return ('0' + result % 10)
    }

    private fun normalize(str: String) =
        Normalizer.normalize(str, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "").toUpperCase()

}

class MrzDate(
    private val day: Int,
    private val month: Int,
    private val year: Int
) {
    fun toMrz() = String.format("%02d%02d%02d", year, month, day)
}

class CustomMrz constructor(mrz: String) {

    var documentType: String? = null
    var type: String? = null
    var dateOfBirth: String? = null
    var lastName: String? = null
    var issuer: String? = null
    var documentNumber: String? = null
    var nationality: String? = null
    var expirationDate: String? = null
    var mrzInfo: String? = null
    var string: String? = null
    var documentNumberCheck = 0.toChar()
    var dateOfBirthCheck = 0.toChar()
    var expirationDateCheck = 0.toChar()
    var checkDigit = 0.toChar()
    var checkDigitInput: String? = null
    var optionB: String? = null
    var optionA: String? = null
    var gender = 0.toChar()

    private var givenNames: Array<String?>? = null

    init {
        parse(mrz)
    }

    fun getFirstName() = givenNames?.joinToString(" ")

    private fun parse(s: String) {
        parse3Lines(s.substring(0, 30), s.substring(30, 60), s.substring(60, 90))
    }

    private fun parse3Lines(line1: String, line2: String, line3: String) {
        documentType = line1.substring(0, 1)
        type = line1.substring(1, 2).replace("<", "")
        issuer = line1.substring(2, 5).replace("<", "")
        val endOfDocumentNumber: Int
        if (line1[14] == '<') {
            endOfDocumentNumber = line1.indexOf(60.toChar(), 15) - 1
            if (endOfDocumentNumber < 0) {
                throw Exception("Document Number not terminated with <")
            }
            documentNumber = line1.substring(5, 14) + line1.substring(15, endOfDocumentNumber)
        } else {
            endOfDocumentNumber = 14
            documentNumber = line1.substring(5, endOfDocumentNumber)
        }
        documentNumberCheck = line1[endOfDocumentNumber]

        optionA = line1.substring(endOfDocumentNumber + 1, 29).replace("<", "")

        mrzInfo = documentNumber + line1.substring(endOfDocumentNumber, endOfDocumentNumber + 1)
        dateOfBirth = line2.substring(0, 6)
        dateOfBirthCheck = line2[6]
        mrzInfo += line2.substring(0, 7)
        gender = line2[7]
        expirationDate = line2.substring(8, 14)
        expirationDateCheck = line2[14]
        mrzInfo += line2.substring(8, 15)
        nationality = line2.substring(15, 18).replace("<", "")
        checkDigitInput =
            line1.substring(5, 30) + line2.substring(0, 7) + line2.substring(8, 15) + line2.substring(18, 29)
        checkDigit = line2[29]
        optionB = line2.substring(18, 29).replace("<", "")
        var endOfLastName = line3.indexOf("<<")
        if (endOfLastName <= 0) {
            endOfLastName = if (line3[29] == '<') {
                29
            } else {
                30
            }
            givenNames = arrayOfNulls(0)
        } else {
            var endOfGivenNames = line3.indexOf("<<", endOfLastName + 2)
            if (endOfGivenNames == endOfLastName + 2) {
                givenNames = arrayOfNulls(0)
            } else {
                if (endOfGivenNames < endOfLastName + 2) {
                    endOfGivenNames = 30
                }
                givenNames =
                    line3.substring(endOfLastName + 2, endOfGivenNames).split("<".toRegex()).toTypedArray()
            }
        }
        lastName = line3.substring(0, endOfLastName)
    }
}