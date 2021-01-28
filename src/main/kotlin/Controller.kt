interface IMrzController {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setBirthDate(birthDate: String)
    fun setGender(gender: String)
    fun setDocumentType(documentType: String)
    fun setDocumentNumber(documentNumber: String)
    fun setExpiryDate(expiryDate: String)
    fun setOptionalA(optionalA: String)
    fun setOptionalB(optionalB: String)
    fun setNationality(nationality: String)
    fun setIssuingCountry(issuingCountry: String)
    fun generateMRZ(): String
    fun extractMRZ(mrz: String): CustomMrz
}

class MrzController : IMrzController {

    private var documentType: String? = null
    private var lastName: String? = null
    private var firstName: String? = null
    private var issuingCountry: String? = null
    private var nationality: String? = null
    private var documentNumber: String? = null
    private var gender: String? = null
    private var birthDate: String? = null
    private var expirationDate: String? = null
    private var optionalA: String? = null
    private var optionalB: String? = null

    override fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    override fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    override fun setBirthDate(birthDate: String) {
        this.birthDate = birthDate
    }

    override fun setGender(gender: String) {
        this.gender = gender
    }

    override fun setDocumentType(documentType: String) {
        this.documentType = documentType
    }

    override fun setDocumentNumber(documentNumber: String) {
        this.documentNumber = documentNumber
    }

    override fun setExpiryDate(expiryDate: String) {
        this.expirationDate = expiryDate
    }

    override fun setOptionalA(optionalA: String) {
        this.optionalA = optionalA
    }

    override fun setOptionalB(optionalB: String) {
        this.optionalB = optionalB
    }

    override fun setNationality(nationality: String) {
        this.nationality = nationality
    }

    override fun setIssuingCountry(issuingCountry: String) {
        this.issuingCountry = issuingCountry
    }

    override fun generateMRZ(): String {
        checkInputValidity()

        val dateOfBirth = MrzDate(
            birthDate!!.substring(4).toInt(),
            birthDate!!.substring(2, 4).toInt(),
            birthDate!!.substring(0, 2).toInt(),
        )

        val dateOfExpiry = MrzDate(
            expirationDate!!.substring(4).toInt(),
            expirationDate!!.substring(2, 4).toInt(),
            expirationDate!!.substring(0, 2).toInt(),
        )

        return MRZGenerator.encode(
            documentType!!,
            lastName!!,
            firstName!!,
            issuingCountry!!,
            nationality!!,
            documentNumber!!,
            gender!!,
            dateOfBirth,
            dateOfExpiry,
            optionalA,
            optionalB
        )
    }

    override fun extractMRZ(mrz: String): CustomMrz {
        val transformedMrz = mrz.replace("\n", "")
            .replace(" ", "")
            .replace("\r\n", "")

        if (transformedMrz.length != 90)
            throw Exception("MRZ is in wrong length")

        return CustomMrz(transformedMrz)
    }

    private fun checkInputValidity() {
        if (firstName.isNullOrBlank())
            throw Exception("First name is blank")

        if (lastName.isNullOrBlank())
            throw Exception("Last name is blank")

        if (birthDate.isNullOrBlank())
            throw Exception("Birth date is blank")

        if (birthDate?.length != 6)
            throw Exception("Birth date length should be 6")

        if (gender.isNullOrBlank())
            throw Exception("Gender is blank")

        if (gender?.length != 1)
            throw Exception("Gender length should be 1")

        if (documentType.isNullOrBlank())
            throw Exception("Document type is blank")

        if (documentType?.length ?: 0 > 2)
            throw Exception("Document type length should be at maximum 2")

        if (documentNumber.isNullOrBlank())
            throw Exception("Document number is blank")

        if (expirationDate.isNullOrBlank())
            throw Exception("Expiry date is blank")

        if (expirationDate?.length != 6)
            throw Exception("Expiry date length should be 6")

        if (optionalA.isNullOrBlank())
            throw Exception("OptionalA is blank")

        if (optionalB.isNullOrBlank())
            throw Exception("OptionalB is blank")

        if (nationality.isNullOrBlank())
            throw Exception("Nationality is blank")

        if (nationality?.length != 3)
            throw Exception("Nationality length should be at maximum 3")

        if (issuingCountry.isNullOrBlank())
            throw Exception("Issuing country is blank")

        if (issuingCountry?.length != 3)
            throw Exception("Issuing country length should be at maximum 3")
    }
}