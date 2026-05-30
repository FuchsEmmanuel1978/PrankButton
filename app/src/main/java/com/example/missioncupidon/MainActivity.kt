package com.example.missioncupidon

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : ComponentActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imagePreview: ImageView
    private lateinit var startAppAd: StartAppAd
    private var creationScreenOpenCount = 0

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imagePreview.setImageURI(uri)
        } else {
            Toast.makeText(this, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StartAppSDK.init(this, "205489527", false)
        startAppAd = StartAppAd(this)
        showSplashScreen()
    }

    private fun showSplashScreen() {
        val splashImage = ImageView(this).apply {
            setImageResource(R.drawable.splash_prank)
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(splashImage)
        Handler(Looper.getMainLooper()).postDelayed({
            if (shouldShowTutorial()) showTutorialScreen() else showCategorySelection()
        }, 1800)
    }

    private fun shouldShowTutorial(): Boolean {
        val prefs = getSharedPreferences("moodpop_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("show_tutorial_first_launch", true)
    }

    private fun markTutorialAsSeen() {
        val prefs = getSharedPreferences("moodpop_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("show_tutorial_first_launch", false).apply()
    }

    private fun showTutorialScreen() {
        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable("#FF5F9E", "#7C3AED")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(42), dp(22), dp(28))
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = cardDrawable()
            setPadding(dp(22), dp(28), dp(22), dp(24))
        }

        val emoji = TextView(this).apply {
            text = "💫"
            textSize = 58f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(8))
        }

        val title = TextView(this).apply {
            text = getString(R.string.tutorial_title)
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(18))
        }

        val steps = listOf(
            getString(R.string.tutorial_step_0),
            getString(R.string.tutorial_step_1),
            getString(R.string.tutorial_step_2),
            getString(R.string.tutorial_step_3),
            getString(R.string.tutorial_step_4)
        )

        val stepsBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = tutorialBoxDrawable()
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        steps.forEach { step ->
            stepsBox.addView(TextView(this).apply {
                text = step
                textSize = 18f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                setPadding(0, dp(8), 0, dp(8))
            })
        }

        val startButton = Button(this).apply {
            text = getString(R.string.tutorial_start)
            textSize = 19f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = roundedButtonDrawable("#16A34A")
            isAllCaps = false
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setOnClickListener {
                markTutorialAsSeen()
                showCategorySelection()
            }
        }

        val skipButton = Button(this).apply {
            text = getString(R.string.tutorial_skip)
            textSize = 15f
            setTextColor(Color.WHITE)
            background = roundedButtonDrawable("#374151")
            isAllCaps = false
            setPadding(dp(14), dp(12), dp(14), dp(12))
            setOnClickListener {
                markTutorialAsSeen()
                showCategorySelection()
            }
        }

        card.addView(emoji)
        card.addView(title)
        card.addView(stepsBox, matchWrapWithBottomMargin(dp(20)))
        card.addView(startButton, matchWrapWithBottomMargin(dp(12)))
        card.addView(skipButton)
        layout.addView(card)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun showCategorySelection() {
        selectedImageUri = null
        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable("#FF5F9E", "#2563EB")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(32), dp(20), dp(24))
        }

        val topCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = cardDrawable()
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }

        topCard.addView(TextView(this).apply {
            text = getString(R.string.app_title)
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        })

        topCard.addView(TextView(this).apply {
            text = "Que veux-tu envoyer aujourd’hui ?"
            textSize = 16f
            setTextColor(Color.parseColor("#FCE7F3"))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(4))
        })

        layout.addView(topCard, matchWrapWithBottomMargin(dp(20)))

        MessageCategoryCatalog.all().forEach { category ->
            val button = Button(this).apply {
                text = "${category.emoji}  ${category.name}"
                textSize = 20f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                background = roundedButtonDrawable(category.colors.accent)
                setPadding(dp(18), dp(16), dp(18), dp(16))
                isAllCaps = false
                setOnClickListener { showInteractionSelection(category) }
            }
            layout.addView(button, matchWrapWithBottomMargin(dp(14)))
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun showInteractionSelection(category: MessageCategory) {
        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable(category.colors.appStart, category.colors.appEnd)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(24))
        }

        val headerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = cardDrawable()
            setPadding(dp(20), dp(22), dp(20), dp(22))
        }

        val backButton = Button(this).apply {
            text = "← Changer d’émotion"
            textSize = 15f
            setTextColor(Color.WHITE)
            background = roundedButtonDrawable("#374151")
            isAllCaps = false
            setOnClickListener { showCategorySelection() }
        }

        headerCard.addView(backButton)
        headerCard.addView(TextView(this).apply {
            text = "${category.emoji} ${category.name}"
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(8))
        })
        headerCard.addView(TextView(this).apply {
            text = "Comment veux-tu révéler ton message ?"
            textSize = 16f
            setTextColor(Color.parseColor("#F9FAFB"))
            gravity = Gravity.CENTER
        })
        layout.addView(headerCard, matchWrapWithBottomMargin(dp(18)))

        InteractionTypeCatalog.recommendedFor(category).forEachIndexed { index, interaction ->
            if (index == 0) layout.addView(sectionTitle("Recommandés pour ${category.emoji} ${category.name}"))
            if (index == category.recommendedInteractionIds.size) layout.addView(sectionTitle("Autres formats"))
            val button = Button(this).apply {
                text = "${interaction.emoji}  ${interaction.name}\n${interaction.description}"
                textSize = 16f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                background = roundedButtonDrawable(category.colors.accent)
                setPadding(dp(18), dp(16), dp(18), dp(16))
                isAllCaps = false
                setOnClickListener { showCreationScreen(category, interaction) }
            }
            layout.addView(button, matchWrapWithBottomMargin(dp(14)))
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun showCreationScreen(category: MessageCategory, interaction: InteractionType) {
        creationScreenOpenCount++
        if (creationScreenOpenCount >= 2) {
            startAppAd.showAd()
            startAppAd.loadAd()
        }

        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable(category.colors.appStart, category.colors.appEnd)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(24), dp(18), dp(24))
        }

        val headerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardDrawable()
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        headerCard.addView(Button(this).apply {
            text = "← Changer de format"
            setTextColor(Color.WHITE)
            textSize = 16f
            background = roundedButtonDrawable("#374151")
            isAllCaps = false
            setOnClickListener { showInteractionSelection(category) }
        })
        headerCard.addView(TextView(this).apply {
            text = "${category.emoji} ${category.name} + ${interaction.emoji} ${interaction.name}"
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(8))
        })
        headerCard.addView(TextView(this).apply {
            text = getString(R.string.edit_instruction)
            textSize = 15f
            setTextColor(Color.parseColor("#F9FAFB"))
            gravity = Gravity.CENTER
        })

        val formCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardDrawable()
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        val defaults = category.defaults
        val titleInput = styledEditText(getString(R.string.field_title), defaults.title)
        val questionInput = styledEditText(getString(R.string.field_question), defaults.question)
        val primaryInput = styledEditText(getString(R.string.field_positive_button), defaults.primaryButton)
        val secondaryInput = styledEditText(getString(R.string.field_escaping_button), defaults.secondaryButton)
        val finalMessageInput = styledEditText(getString(R.string.field_final_message), defaults.finalMessage)

        imagePreview = ImageView(this).apply {
            adjustViewBounds = true
            minimumHeight = dp(180)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = imageFrameDrawable()
            setImageResource(android.R.drawable.ic_menu_gallery)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        formCard.addView(label(getString(R.string.field_title)))
        formCard.addView(titleInput, matchWrapWithBottomMargin(dp(12)))
        formCard.addView(label(getString(R.string.field_question)))
        formCard.addView(questionInput, matchWrapWithBottomMargin(dp(12)))
        formCard.addView(label(getString(R.string.field_positive_button)))
        formCard.addView(primaryInput, matchWrapWithBottomMargin(dp(12)))
        if (interaction.needsSecondaryButton) {
            formCard.addView(label(getString(R.string.field_escaping_button)))
            formCard.addView(secondaryInput, matchWrapWithBottomMargin(dp(12)))
        }
        formCard.addView(label(getString(R.string.field_final_message)))
        formCard.addView(finalMessageInput, matchWrapWithBottomMargin(dp(16)))
        formCard.addView(label(getString(R.string.selected_photo)))
        formCard.addView(imagePreview, matchWrapWithBottomMargin(dp(14)))
        formCard.addView(Button(this).apply {
            text = getString(R.string.choose_photo)
            setTextColor(Color.WHITE)
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            background = roundedButtonDrawable(category.colors.accent)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isAllCaps = false
            setOnClickListener {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }, matchWrapWithBottomMargin(dp(14)))
        formCard.addView(Button(this).apply {
            text = getString(R.string.create_send_whatsapp)
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            background = roundedButtonDrawable("#16A34A")
            setPadding(dp(18), dp(16), dp(18), dp(16))
            isAllCaps = false
            setOnClickListener {
                createAndShareHtml(
                    GeneratedExperience(
                        category = category,
                        interaction = interaction,
                        title = titleInput.text.toString().ifBlank { defaults.title },
                        question = questionInput.text.toString().ifBlank { defaults.question },
                        primaryButton = primaryInput.text.toString().ifBlank { defaults.primaryButton },
                        secondaryButton = secondaryInput.text.toString().ifBlank { defaults.secondaryButton },
                        finalMessage = finalMessageInput.text.toString().ifBlank { defaults.finalMessage },
                        imageBase64 = ""
                    )
                )
            }
        })

        layout.addView(headerCard, matchWrapWithBottomMargin(dp(18)))
        layout.addView(formCard)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun createAndShareHtml(experienceWithoutImage: GeneratedExperience) {
        try {
            if (selectedImageUri == null) {
                Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_LONG).show()
                return
            }
            val imageBase64 = convertImageToBase64(selectedImageUri!!)
            val experience = experienceWithoutImage.copy(imageBase64 = imageBase64)
            val html = GeneratedExperienceHtmlGenerator.generate(experience)
            val outputFile = File(cacheDir, "moodpop.html")
            outputFile.writeText(html, Charsets.UTF_8)
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", outputFile)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/html"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.whatsapp_not_installed), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_prefix) + (e.message ?: ""), Toast.LENGTH_LONG).show()
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Impossible de lire l'image")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 82, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    private fun styledEditText(hint: String, defaultText: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            setText(defaultText)
            setTextColor(Color.parseColor("#111827"))
            setHintTextColor(Color.parseColor("#9CA3AF"))
            textSize = 16f
            background = inputDrawable()
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
    }

    private fun label(text: String): TextView = TextView(this).apply {
        this.text = text
        setTextColor(Color.parseColor("#374151"))
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        setPadding(0, 0, 0, dp(6))
    }

    private fun sectionTitle(text: String): TextView = TextView(this).apply {
        this.text = text
        setTextColor(Color.WHITE)
        textSize = 17f
        typeface = Typeface.DEFAULT_BOLD
        setPadding(0, dp(8), 0, dp(10))
    }

    private fun gradientDrawable(start: String, end: String): GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Color.parseColor(start), Color.parseColor(end))
    )

    private fun roundedButtonDrawable(color: String): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(18).toFloat()
        setColor(Color.parseColor(color))
    }

    private fun inputDrawable(): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(16).toFloat()
        setColor(Color.WHITE)
        setStroke(dp(1), Color.parseColor("#E5E7EB"))
    }

    private fun cardDrawable(): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(24).toFloat()
        setColor(Color.parseColor("#22FFFFFF"))
        setStroke(dp(1), Color.parseColor("#55FFFFFF"))
    }

    private fun imageFrameDrawable(): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(18).toFloat()
        setColor(Color.parseColor("#FFFFFFFF"))
        setStroke(dp(1), Color.parseColor("#E5E7EB"))
    }

    private fun tutorialBoxDrawable(): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(22).toFloat()
        setColor(Color.parseColor("#33FFFFFF"))
        setStroke(dp(1), Color.parseColor("#66FFFFFF"))
    }

    private fun matchWrapWithBottomMargin(bottom: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = bottom }
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        resources.displayMetrics
    ).toInt()
}
