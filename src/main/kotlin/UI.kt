import org.jdesktop.swingx.prompt.PromptSupport
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter


class UI(private val controller: IMrzController) : JFrame("MRZ generator") {

    private lateinit var mrzTextField: JTextArea
    private lateinit var extractMrzTextField: JTextArea
    private lateinit var firstNameTextField: JTextField
    private lateinit var lastNameTextField: JTextField
    private lateinit var birthDateTextField: JTextField
    private lateinit var genderTextField: JTextField
    private lateinit var documentTypeTextField: JTextField
    private lateinit var documentNumberTextField: JTextField
    private lateinit var expirationDateTextField: JTextField
    private lateinit var optionalATextField: JTextField
    private lateinit var optionalBTextField: JTextField
    private lateinit var nationalityTextField: JTextField
    private lateinit var issuingCountryTextField: JTextField
    private lateinit var generateButton: JButton
    private lateinit var extractButton: JButton

    companion object {
        private const val DEFAULT_INSET = 5
    }

    init {
        initUI()

        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
        setLocationRelativeTo(null)
        isResizable = false
    }

    private fun initUI() {
        val layoutManager = GridBagLayout()
        val mainContent = JPanel().apply {
            layout = layoutManager
            border = BorderFactory.createEmptyBorder(5, 20, 20, 20)
            background = Color.WHITE
        }

        val fieldWidth = 20

        extractMrzTextField = JTextArea(5, fieldWidth * 2).apply {
            PromptSupport.setPrompt("enter MRZ for extracting fields", this)

            wrapStyleWord = true
        }
        val extractMrzScrollPane = JScrollPane(extractMrzTextField).apply {
            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                gridwidth = 2
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(10, DEFAULT_INSET, 10, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(extractMrzScrollPane)

        extractButton = JButton("Extract").apply {

            addActionListener { extractFields() }

            val constraints = GridBagConstraints().apply {
                gridx = 2
                gridy = 0
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(10, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(extractButton)

        firstNameTextField = JTextField(fieldWidth).apply {
            JLabel("First name").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 0
                    gridy = 1
                    anchor = GridBagConstraints.NORTHWEST
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setFirstName(this.text) }

            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 2
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, 0, DEFAULT_INSET, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(firstNameTextField)

        lastNameTextField = JTextField(fieldWidth).apply {
            JLabel("Last Name").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 1
                    gridy = 1
                    anchor = GridBagConstraints.NORTHWEST
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setLastName(this.text) }

            val constraints = GridBagConstraints().apply {
                gridx = 1
                gridy = 2
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, 0, DEFAULT_INSET, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(lastNameTextField)

        birthDateTextField = JTextField(fieldWidth).apply {
            JLabel("Birth date").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 0
                    gridy = 3
                    anchor = GridBagConstraints.NORTHWEST
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            PromptSupport.setPrompt("yyMMdd", this)
            addTextListener { controller.setBirthDate(text) }
            addFilter()

            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 4
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, 0, 0, DEFAULT_INSET)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(birthDateTextField)

        genderTextField = JTextField(fieldWidth).apply {
            JLabel("Gender").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 1
                    gridy = 3
                    anchor = GridBagConstraints.NORTHWEST
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            PromptSupport.setPrompt("X", this)
            addTextListener { controller.setGender(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 1
                gridy = 4
                gridwidth = 3
                anchor = GridBagConstraints.NORTHWEST
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(genderTextField)

        documentTypeTextField = JTextField(fieldWidth).apply {
            JLabel("Document type").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 0
                    gridy = 5

                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, 0, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            PromptSupport.setPrompt("XX", this)
            addTextListener { controller.setDocumentType(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 6
                anchor = GridBagConstraints.NORTHWEST
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(documentTypeTextField)

        documentNumberTextField = JTextField(fieldWidth).apply {
            JLabel("Document number").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 1
                    gridy = 5
                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, 0, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setDocumentNumber(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 1
                gridy = 6
                anchor = GridBagConstraints.NORTHWEST
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(documentNumberTextField)

        expirationDateTextField = JTextField(fieldWidth).apply {
            JLabel("Expiry date").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 2
                    gridy = 5
                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, 0, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            PromptSupport.setPrompt("yyMMdd", this)
            addTextListener { controller.setExpiryDate(text) }
            addFilter()

            val constraints = GridBagConstraints().apply {
                gridx = 2
                gridy = 6

                anchor = GridBagConstraints.NORTHWEST
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(expirationDateTextField)

        optionalATextField = JTextField(fieldWidth).apply {
            JLabel("Optional A").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 0
                    gridy = 7

                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, DEFAULT_INSET, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setOptionalA(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 8

                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(optionalATextField)

        optionalBTextField = JTextField(fieldWidth).apply {
            JLabel("Optional B").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 1
                    gridy = 7
                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, 0, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setOptionalB(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 1
                gridy = 8
                anchor = GridBagConstraints.NORTHWEST
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(optionalBTextField)

        nationalityTextField = JTextField(fieldWidth).apply {
            JLabel("Nationality").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 0
                    gridy = 9
                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, DEFAULT_INSET, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setNationality(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 10
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(nationalityTextField)

        issuingCountryTextField = JTextField(fieldWidth).apply {
            JLabel("Issuing country").apply {

                val constraints = GridBagConstraints().apply {
                    gridx = 1
                    gridy = 9
                    anchor = GridBagConstraints.NORTHWEST
                    insets = Insets(DEFAULT_INSET, DEFAULT_INSET, 0, 0)
                }
                layoutManager.setConstraints(this, constraints)
                mainContent.add(this)
            }

            addTextListener { controller.setIssuingCountry(text) }

            val constraints = GridBagConstraints().apply {
                gridx = 1
                gridy = 10
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(0, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(issuingCountryTextField)

        generateButton = JButton("Generate").apply {

            addActionListener { generateMRZ() }

            val constraints = GridBagConstraints().apply {
                gridx = 2
                gridy = 12
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(10, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(generateButton)

        mrzTextField = JTextArea(5, fieldWidth * 2).apply {
            PromptSupport.setPrompt("MRZ", this)

            wrapStyleWord = true
        }
        val scrollPane = JScrollPane(mrzTextField).apply {
            val constraints = GridBagConstraints().apply {
                gridx = 0
                gridy = 12
                gridwidth = 2
                anchor = GridBagConstraints.NORTHWEST
                insets = Insets(10, DEFAULT_INSET, 0, 0)
            }
            layoutManager.setConstraints(this, constraints)
        }
        mainContent.add(scrollPane)

        contentPane = mainContent
    }

    private fun extractFields() {
        try {
            val mrz = controller.extractMRZ(extractMrzTextField.text)
            firstNameTextField.text = mrz.getFirstName()
            lastNameTextField.text = mrz.lastName
            birthDateTextField.text = mrz.dateOfBirth
            genderTextField.text = mrz.gender.toString()
            documentTypeTextField.text = mrz.documentType
            documentNumberTextField.text = mrz.documentNumber
            expirationDateTextField.text = mrz.expirationDate
            optionalATextField.text = mrz.optionA
            optionalBTextField.text = mrz.optionB
            nationalityTextField.text = mrz.nationality
            issuingCountryTextField.text = mrz.issuer
        } catch (e: Exception) {
            showMessage(e.message)
        }
    }

    private fun generateMRZ() {
        try {
            val mrz = controller.generateMRZ()
            mrzTextField.text = mrz
        } catch (e: Exception) {
            showMessage(e.message)
        }
    }

    private fun showMessage(message: String?) {
        message?.let { JOptionPane.showMessageDialog(null, message, null, JOptionPane.PLAIN_MESSAGE) }
    }

    private fun JTextField.addTextListener(func: () -> Unit) {
        document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) = func()

            override fun removeUpdate(e: DocumentEvent?) = func()

            override fun insertUpdate(e: DocumentEvent?) = func()
        })
    }

    private fun JTextField.addFilter(regexString: String = "\\d*") {
        (document as AbstractDocument).documentFilter = object : DocumentFilter() {
            var regEx = regexString.toRegex()

            @Throws(BadLocationException::class)
            override fun replace(fb: FilterBypass?, offset: Int, length: Int, text: String?, attrs: AttributeSet?) {
                text?.let {
                    if (!regEx.matches(it))
                        return
                }

                super.replace(fb, offset, length, text, attrs)
            }
        }
    }
}