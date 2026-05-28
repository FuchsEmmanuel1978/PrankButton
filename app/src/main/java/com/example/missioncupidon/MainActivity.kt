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
import java.util.Locale

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

    data class MoodTemplate(
        val name: String,
        val emoji: String,
        val title: String,
        val question: String,
        val yesText: String,
        val noText: String,
        val finalMessage: String,
        val colorStart: String,
        val colorEnd: String,
        val accentColor: String,
        val pageStartColor: String,
        val pageEndColor: String,
        val pageAccentColor: String,
        val fallingSymbols: List<String>,
        val finalEmoji: String
    )

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
            if (shouldShowTutorial()) {
                showTutorialScreen()
            } else {
                showMoodSelection()
            }
        }, 1800)
    }

    private fun getMoodTemplates(): List<MoodTemplate> = listOf(
        MoodTemplate(
            getString(R.string.mood_laugh_name),
            "😂",
            getString(R.string.mood_laugh_title),
            getString(R.string.mood_laugh_question),
            getString(R.string.mood_laugh_yes),
            getString(R.string.mood_laugh_no),
            getString(R.string.mood_laugh_final),
            "#F97316",
            "#EC4899",
            "#EA580C",
            "#f97316",
            "#ec4899",
            "#ea580c",
            listOf("😂", "🤣", "😄", "✨", "🎉"),
            "😂"
        ),
        MoodTemplate(
            getString(R.string.mood_love_name),
            "❤️",
            getString(R.string.mood_love_title),
            getString(R.string.mood_love_question),
            getString(R.string.mood_love_yes),
            getString(R.string.mood_love_no),
            getString(R.string.mood_love_final),
            "#FF5F9E",
            "#A855F7",
            "#FF3D81",
            "#ff5f9e",
            "#a855f7",
            "#ff3d81",
            listOf("❤️", "💖", "💕", "💘", "✨"),
            "💖"
        ),
        MoodTemplate(
            getString(R.string.mood_birthday_name),
            "🎂",
            getString(R.string.mood_birthday_title),
            getString(R.string.mood_birthday_question),
            getString(R.string.mood_birthday_yes),
            getString(R.string.mood_birthday_no),
            getString(R.string.mood_birthday_final),
            "#EC4899",
            "#F97316",
            "#DB2777",
            "#ec4899",
            "#f97316",
            "#db2777",
            listOf("🎂", "🎉", "🎁", "🥳", "✨"),
            "🎉"
        ),
        MoodTemplate(
            getString(R.string.mood_thanks_name),
            "🙏",
            getString(R.string.mood_thanks_title),
            getString(R.string.mood_thanks_question),
            getString(R.string.mood_thanks_yes),
            getString(R.string.mood_thanks_no),
            getString(R.string.mood_thanks_final),
            "#14B8A6",
            "#22C55E",
            "#0F766E",
            "#14b8a6",
            "#22c55e",
            "#0f766e",
            listOf("🙏", "💛", "✨", "🌟", "💐"),
            "🙏"
        ),
        MoodTemplate(
            getString(R.string.mood_comfort_name),
            "🤗",
            getString(R.string.mood_comfort_title),
            getString(R.string.mood_comfort_question),
            getString(R.string.mood_comfort_yes),
            getString(R.string.mood_comfort_no),
            getString(R.string.mood_comfort_final),
            "#60A5FA",
            "#A78BFA",
            "#2563EB",
            "#60a5fa",
            "#a78bfa",
            "#2563eb",
            listOf("🤗", "💙", "☁️", "✨", "🌙"),
            "🤗"
        ),
        MoodTemplate(
            getString(R.string.mood_sorry_name),
            "😢",
            getString(R.string.mood_sorry_title),
            getString(R.string.mood_sorry_question),
            getString(R.string.mood_sorry_yes),
            getString(R.string.mood_sorry_no),
            getString(R.string.mood_sorry_final),
            "#94A3B8",
            "#38BDF8",
            "#64748B",
            "#94a3b8",
            "#38bdf8",
            "#64748b",
            listOf("😢", "🤍", "🌧️", "🕊️", "✨"),
            "🤍"
        ),
        MoodTemplate(
            getString(R.string.mood_motivate_name),
            "🔥",
            getString(R.string.mood_motivate_title),
            getString(R.string.mood_motivate_question),
            getString(R.string.mood_motivate_yes),
            getString(R.string.mood_motivate_no),
            getString(R.string.mood_motivate_final),
            "#EF4444",
            "#F59E0B",
            "#DC2626",
            "#ef4444",
            "#f59e0b",
            "#dc2626",
            listOf("🔥", "⚡", "🏆", "💪", "🚀"),
            "🔥"
        ),
        MoodTemplate(
            getString(R.string.mood_surprise_name),
            "🎁",
            getString(R.string.mood_surprise_title),
            getString(R.string.mood_surprise_question),
            getString(R.string.mood_surprise_yes),
            getString(R.string.mood_surprise_no),
            getString(R.string.mood_surprise_final),
            "#8B5CF6",
            "#EC4899",
            "#7C3AED",
            "#8b5cf6",
            "#ec4899",
            "#7c3aed",
            listOf("🎁", "✨", "🎉", "💫", "😮"),
            "🎁"
        ),
        MoodTemplate(
            getString(R.string.mood_declaration_name),
            "💌",
            getString(R.string.mood_declaration_title),
            getString(R.string.mood_declaration_question),
            getString(R.string.mood_declaration_yes),
            getString(R.string.mood_declaration_no),
            getString(R.string.mood_declaration_final),
            "#F472B6",
            "#8B5CF6",
            "#DB2777",
            "#f472b6",
            "#8b5cf6",
            "#db2777",
            listOf("💌", "❤️", "🌹", "✨", "💫"),
            "💌"
        ),
        MoodTemplate(
            getString(R.string.mood_prank_name),
            "😈",
            getString(R.string.mood_prank_title),
            getString(R.string.mood_prank_question),
            getString(R.string.mood_prank_yes),
            getString(R.string.mood_prank_no),
            getString(R.string.mood_prank_final),
            "#7C3AED",
            "#111827",
            "#A855F7",
            "#7c3aed",
            "#111827",
            "#a855f7",
            listOf("😈", "😂", "💥", "🌀", "🎭"),
            "😈"
        )
    )


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
            text = "😄"
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
            val stepView = TextView(this).apply {
                text = step
                textSize = 18f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                setPadding(0, dp(8), 0, dp(8))
            }
            stepsBox.addView(stepView)
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
                showMoodSelection()
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
                showMoodSelection()
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

    private fun showMoodSelection() {
        selectedImageUri = null

        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable("#FF5F9E", "#7C3AED")

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

        val title = TextView(this).apply {
            text = getString(R.string.app_title)
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = getString(R.string.choose_theme)
            textSize = 16f
            setTextColor(Color.parseColor("#FCE7F3"))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(4))
        }

        topCard.addView(title)
        topCard.addView(subtitle)
        layout.addView(topCard, matchWrapWithBottomMargin(dp(20)))

        getMoodTemplates().forEach { theme ->
            val button = Button(this).apply {
                text = "${theme.emoji}  ${theme.name}"
                textSize = 20f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                background = roundedButtonDrawable(theme.accentColor)
                setPadding(dp(18), dp(16), dp(18), dp(16))
                isAllCaps = false
                setOnClickListener {
                    showCreationScreen(theme)
                }
            }

            layout.addView(button, matchWrapWithBottomMargin(dp(14)))
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun showCreationScreen(theme: MoodTemplate) {
        creationScreenOpenCount++

        if (creationScreenOpenCount >= 2) {
            startAppAd.showAd()
            startAppAd.loadAd()
        }

        val scrollView = ScrollView(this)
        scrollView.background = gradientDrawable(theme.colorStart, theme.colorEnd)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(24), dp(18), dp(24))
        }

        val headerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardDrawable()
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val backButton = Button(this).apply {
            text = getString(R.string.change_theme)
            setTextColor(Color.WHITE)
            textSize = 16f
            background = roundedButtonDrawable("#374151")
            isAllCaps = false
            setOnClickListener {
                showMoodSelection()
            }
        }

        val titleApp = TextView(this).apply {
            text = "${theme.emoji} ${theme.name}"
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(8))
        }

        val smallText = TextView(this).apply {
            text = getString(R.string.edit_instruction)
            textSize = 15f
            setTextColor(Color.parseColor("#F9FAFB"))
            gravity = Gravity.CENTER
        }

        headerCard.addView(backButton)
        headerCard.addView(titleApp)
        headerCard.addView(smallText)

        val formCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardDrawable()
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        val titleInput = styledEditText(getString(R.string.field_title), theme.title)
        val questionInput = styledEditText(getString(R.string.field_question), theme.question)
        val yesInput = styledEditText(getString(R.string.field_positive_button), theme.yesText)
        val noInput = styledEditText(getString(R.string.field_escaping_button), theme.noText)
        val finalMessageInput = styledEditText(getString(R.string.field_final_message), theme.finalMessage)

        imagePreview = ImageView(this).apply {
            adjustViewBounds = true
            minimumHeight = dp(180)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = imageFrameDrawable()
            setImageResource(android.R.drawable.ic_menu_gallery)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        val choosePhotoButton = Button(this).apply {
            text = getString(R.string.choose_photo)
            setTextColor(Color.WHITE)
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            background = roundedButtonDrawable(theme.accentColor)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isAllCaps = false
            setOnClickListener {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }

        val sendButton = Button(this).apply {
            text = getString(R.string.create_send_whatsapp)
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            background = roundedButtonDrawable("#16A34A")
            setPadding(dp(18), dp(16), dp(18), dp(16))
            isAllCaps = false
            setOnClickListener {
                val title = titleInput.text.toString().ifBlank { theme.title }
                val question = questionInput.text.toString().ifBlank { theme.question }
                val yesText = yesInput.text.toString().ifBlank { theme.yesText }
                val noText = noInput.text.toString().ifBlank { theme.noText }
                val finalMessage = finalMessageInput.text.toString().ifBlank { theme.finalMessage }

                createAndShareHtml(
                    theme = theme,
                    title = title,
                    question = question,
                    yesText = yesText,
                    noText = noText,
                    finalMessage = finalMessage
                )
            }
        }

        formCard.addView(label(getString(R.string.field_title)))
        formCard.addView(titleInput, matchWrapWithBottomMargin(dp(12)))

        formCard.addView(label(getString(R.string.field_question)))
        formCard.addView(questionInput, matchWrapWithBottomMargin(dp(12)))

        formCard.addView(label(getString(R.string.field_positive_button)))
        formCard.addView(yesInput, matchWrapWithBottomMargin(dp(12)))

        formCard.addView(label(getString(R.string.field_escaping_button)))
        formCard.addView(noInput, matchWrapWithBottomMargin(dp(12)))

        formCard.addView(label(getString(R.string.field_final_message)))
        formCard.addView(finalMessageInput, matchWrapWithBottomMargin(dp(16)))

        formCard.addView(label(getString(R.string.selected_photo)))
        formCard.addView(imagePreview, matchWrapWithBottomMargin(dp(14)))
        formCard.addView(choosePhotoButton, matchWrapWithBottomMargin(dp(14)))
        formCard.addView(sendButton)

        layout.addView(headerCard, matchWrapWithBottomMargin(dp(18)))
        layout.addView(formCard)

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun createAndShareHtml(
        theme: MoodTemplate,
        title: String,
        question: String,
        yesText: String,
        noText: String,
        finalMessage: String
    ) {
        try {
            if (selectedImageUri == null) {
                Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_LONG).show()
                return
            }

            val imageBase64 = convertImageToBase64(selectedImageUri!!)
            val html = generateHtml(
                theme = theme,
                title = title,
                question = question,
                yesText = yesText,
                noText = noText,
                finalMessage = finalMessage,
                imageBase64 = imageBase64
            )

            val outputFile = File(cacheDir, "moodpop.html")
            outputFile.writeText(html, Charsets.UTF_8)

            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                outputFile
            )

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
            Toast.makeText(
                this,
                getString(R.string.error_prefix) + (e.message ?: ""),
                Toast.LENGTH_LONG
            ).show()
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

    private fun generateHtml(
        theme: MoodTemplate,
        title: String,
        question: String,
        yesText: String,
        noText: String,
        finalMessage: String,
        imageBase64: String
    ): String {
        val safeTitle = escapeHtml(title)
        val safeQuestion = escapeHtml(question)
        val safeYesText = escapeHtml(yesText)
        val safeNoText = escapeHtml(noText)
        val safeFinalMessage = escapeHtml(finalMessage)
        val safeThemeName = escapeHtml(theme.name)
        val htmlLanguage = Locale.getDefault().language.ifBlank { "fr" }

        val symbolsJsArray = theme.fallingSymbols.joinToString(
            prefix = "[",
            postfix = "]"
        ) { "\"${escapeJs(it)}\"" }

        val primary = theme.pageAccentColor
        val start = theme.pageStartColor
        val end = theme.pageEndColor
        val finalEmoji = escapeHtml(theme.finalEmoji)

        return """
<!DOCTYPE html>
<html lang="$htmlLanguage">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>MoodPop 💫</title>
  <style>
    * { box-sizing: border-box; }

    body {
      margin: 0;
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: "Segoe UI", Arial, sans-serif;
      background: linear-gradient(135deg, $start, $end);
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      overflow: hidden;
      transition: background-image 0.8s ease, background 0.8s ease;
    }

    body.photo-active {
      background-image:
        linear-gradient(rgba(0, 0, 0, 0.10), rgba(0, 0, 0, 0.32)),
        url("data:image/jpeg;base64,$imageBase64");
    }

    body.photo-active::before {
      content: "";
      position: fixed;
      inset: 0;
      background: linear-gradient(135deg, rgba(255,255,255,0.10), rgba(0,0,0,0.18));
      backdrop-filter: blur(1px);
      z-index: 0;
      pointer-events: none;
    }

    .card {
      width: min(92vw, 540px);
      padding: 34px 24px;
      text-align: center;
      background: rgba(255, 255, 255, 0.90);
      border: 1px solid rgba(255,255,255,0.8);
      border-radius: 30px;
      box-shadow: 0 22px 70px rgba(0, 0, 0, 0.26);
      position: relative;
      z-index: 2;
      overflow: hidden;
    }

    .theme-pill {
      display: inline-block;
      padding: 8px 14px;
      border-radius: 999px;
      background: linear-gradient(135deg, $start, $end);
      color: white;
      font-weight: 800;
      margin-bottom: 14px;
      box-shadow: 0 8px 18px rgba(0,0,0,0.18);
    }

    .card.photo-reveal {
      background: transparent;
      box-shadow: none;
      border: none;
      pointer-events: none;
    }

    .card.photo-reveal .buttons,
    .card.photo-reveal .theme-pill {
      display: none;
    }

    .card.photo-reveal h1,
    .card.photo-reveal p,
    .card.photo-reveal .message {
      display: block;
      text-shadow: 0 4px 16px rgba(0, 0, 0, 0.72);
    }

    .card.photo-reveal h1,
    .card.photo-reveal p {
      color: white;
    }

    h1 {
      margin: 0 0 14px;
      color: $primary;
      font-size: clamp(2rem, 7vw, 3.2rem);
      line-height: 1.1;
    }

    p {
      margin: 0 0 30px;
      color: #374151;
      font-size: clamp(1.1rem, 4vw, 1.35rem);
      font-weight: 650;
    }

    .buttons {
      position: relative;
      min-height: 155px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 22px;
      padding: 0 38px;
    }

    button {
      border: none;
      border-radius: 999px;
      padding: 16px 32px;
      font-size: 1.25rem;
      font-weight: 900;
      cursor: pointer;
      transition: transform 0.22s ease, box-shadow 0.22s ease, left 0.22s ease, top 0.22s ease;
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.18);
    }

    #yesBtn {
      background: linear-gradient(135deg, $start, $primary);
      color: white;
      transform: scale(1);
      z-index: 3;
      margin-right: auto;
    }

    #noBtn {
      background: white;
      color: $primary;
      border: 3px solid $primary;
      position: absolute;
      right: 38px;
      top: 50%;
      transform: translateY(-50%);
      z-index: 4;
    }

    .message {
      display: none;
      margin-top: 24px;
      padding: 18px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.22);
      color: white;
      backdrop-filter: blur(3px);
      font-size: 1.28rem;
      font-weight: 900;
    }

    .falling-symbol {
      position: fixed;
      top: -40px;
      color: rgba(255, 255, 255, 0.82);
      font-size: 28px;
      animation: fall linear infinite;
      z-index: 1;
      pointer-events: none;
      filter: drop-shadow(0 4px 8px rgba(0,0,0,0.18));
    }

    .flash {
      position: fixed;
      inset: 0;
      background: radial-gradient(circle, rgba(255,255,255,0.65), transparent 60%);
      z-index: 1;
      animation: flashOut 900ms ease forwards;
      pointer-events: none;
    }

    @keyframes fall {
      to {
        transform: translateY(112vh) rotate(360deg);
      }
    }

    @keyframes flashOut {
      from { opacity: 1; }
      to { opacity: 0; }
    }
  </style>
</head>
<body>
  <main class="card">
    <div class="theme-pill">$finalEmoji $safeThemeName</div>
    <h1>$safeTitle</h1>
    <p>$safeQuestion</p>

    <div class="buttons" id="buttonsBox">
      <button id="yesBtn">$safeYesText</button>
      <button id="noBtn">$safeNoText</button>
    </div>

    <div class="message" id="message">
      $safeFinalMessage
    </div>
  </main>

  <script>
    const noBtn = document.getElementById("noBtn");
    const yesBtn = document.getElementById("yesBtn");
    const message = document.getElementById("message");
    const buttonsBox = document.getElementById("buttonsBox");
    const symbols = $symbolsJsArray;

    let yesScale = 1;

    function moveNoButton() {
      const boxRect = buttonsBox.getBoundingClientRect();
      const noRect = noBtn.getBoundingClientRect();

      const maxX = boxRect.width - noRect.width;
      const maxY = boxRect.height - noRect.height;

      const currentX = noBtn.offsetLeft;
      const currentY = noBtn.offsetTop;
      const minDistance = noRect.width;

      let randomX = currentX;
      let randomY = currentY;
      let attempts = 0;

      do {
        randomX = Math.max(0, Math.random() * maxX);
        randomY = Math.max(0, Math.random() * maxY);
        attempts++;
      } while (
        Math.hypot(randomX - currentX, randomY - currentY) < minDistance &&
        attempts < 80
      );

      if (attempts >= 80) {
        randomX = currentX < maxX / 2 ? maxX : 0;
        randomY = Math.random() * maxY;
      }

      noBtn.style.right = "auto";
      noBtn.style.transform = "none";
      noBtn.style.left = randomX + "px";
      noBtn.style.top = randomY + "px";

      yesScale += 0.18;
      yesBtn.style.transform = `scale(${'$'}{yesScale})`;
    }

    noBtn.addEventListener("mouseenter", moveNoButton);
    noBtn.addEventListener("touchstart", function(event) {
      event.preventDefault();
      moveNoButton();
    });

    noBtn.addEventListener("click", function(event) {
      event.preventDefault();
      moveNoButton();
    });

    yesBtn.addEventListener("click", function() {
      document.body.classList.add("photo-active");
      message.style.display = "block";
      document.querySelector(".card").classList.add("photo-reveal");

      const flash = document.createElement("div");
      flash.className = "flash";
      document.body.appendChild(flash);
      setTimeout(() => flash.remove(), 950);

      for (let i = 0; i < 18; i++) {
        setTimeout(createFallingSymbol, i * 45);
      }
    });

    function createFallingSymbol() {
      const symbol = document.createElement("div");
      symbol.className = "falling-symbol";
      symbol.textContent = symbols[Math.floor(Math.random() * symbols.length)];
      symbol.style.left = Math.random() * 100 + "vw";
      symbol.style.animationDuration = 3.6 + Math.random() * 4.2 + "s";
      symbol.style.fontSize = 18 + Math.random() * 30 + "px";
      symbol.style.opacity = 0.65 + Math.random() * 0.35;
      document.body.appendChild(symbol);

      setTimeout(() => symbol.remove(), 8500);
    }

    setInterval(createFallingSymbol, 430);
  </script>
</body>
</html>
        """.trimIndent()
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

    private fun label(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.parseColor("#374151"))
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, dp(6))
        }
    }

    private fun gradientDrawable(start: String, end: String): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(start), Color.parseColor(end))
        ).apply {
            cornerRadius = 0f
        }
    }

    private fun roundedButtonDrawable(color: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(18).toFloat()
            setColor(Color.parseColor(color))
        }
    }

    private fun inputDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(16).toFloat()
            setColor(Color.WHITE)
            setStroke(dp(1), Color.parseColor("#E5E7EB"))
        }
    }

    private fun cardDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(24).toFloat()
            setColor(Color.parseColor("#22FFFFFF"))
            setStroke(dp(1), Color.parseColor("#55FFFFFF"))
        }
    }

    private fun imageFrameDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(18).toFloat()
            setColor(Color.parseColor("#FFFFFFFF"))
            setStroke(dp(1), Color.parseColor("#E5E7EB"))
        }
    }


    private fun tutorialBoxDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(22).toFloat()
            setColor(Color.parseColor("#33FFFFFF"))
            setStroke(dp(1), Color.parseColor("#66FFFFFF"))
        }
    }

    private fun matchWrapWithBottomMargin(bottom: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = bottom
        }
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#039;")
    }

    private fun escapeJs(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}
