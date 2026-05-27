package com.example.missioncupidon

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imagePreview: ImageView
    private lateinit var chosenPhotoText: TextView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imagePreview.setImageURI(uri)
            chosenPhotoText.text = "Photo sélectionnée ✅"
        } else {
            Toast.makeText(this, "Aucune photo sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootGradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(0xFFFFDDE1.toInt(), 0xFFEE9CA7.toInt())
        )

        val scrollView = ScrollView(this).apply {
            background = rootGradient
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(42), dp(24), dp(32))
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val titleApp = TextView(this).apply {
            text = "Mission Cupidon 💘"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(0xFF8A1744.toInt())
            setPadding(0, 0, 0, dp(8))
        }

        val subtitle = TextView(this).apply {
            text = "Crée une page surprise, choisis une photo, puis envoie-la sur WhatsApp 😄"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(0xFF633044.toInt())
            setPadding(0, 0, 0, dp(22))
        }

        val titleInput = makeEditText("Titre", "Ma chérie ❤️")
        val questionInput = makeEditText("Question", "Est-ce que tu m’aimes ?")
        val finalMessageInput = makeEditText(
            "Message après le Oui",
            "Je le savais 😍 Moi aussi je t’aime fort ❤️"
        )

        imagePreview = ImageView(this).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(android.R.drawable.ic_menu_gallery)
            background = roundedBackground(0x55FFFFFF)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(220)
            ).apply {
                setMargins(0, dp(14), 0, dp(8))
            }
        }

        chosenPhotoText = TextView(this).apply {
            text = "Aucune photo sélectionnée"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(0xFF633044.toInt())
            setPadding(0, 0, 0, dp(10))
        }

        val choosePhotoButton = makeButton("Choisir une photo 📸").apply {
            setOnClickListener {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }

        val sendButton = makeButton("Créer et envoyer sur WhatsApp ❤️").apply {
            textSize = 20f
            setOnClickListener {
                val title = titleInput.text.toString().ifBlank { "Ma chérie ❤️" }
                val question = questionInput.text.toString().ifBlank { "Est-ce que tu m’aimes ?" }
                val finalMessage = finalMessageInput.text.toString()
                    .ifBlank { "Je le savais 😍 Moi aussi je t’aime fort ❤️" }

                createAndShareHtml(title, question, finalMessage)
            }
        }

        layout.addView(titleApp)
        layout.addView(subtitle)
        layout.addView(titleInput)
        layout.addView(questionInput)
        layout.addView(finalMessageInput)
        layout.addView(imagePreview)
        layout.addView(chosenPhotoText)
        layout.addView(choosePhotoButton)
        layout.addView(sendButton)

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun makeEditText(hintText: String, defaultValue: String): EditText {
        return EditText(this).apply {
            hint = hintText
            setText(defaultValue)
            textSize = 17f
            setSingleLine(false)
            minLines = 1
            maxLines = 3
            setTextColor(0xFF4B1D31.toInt())
            setHintTextColor(0xFF9C6077.toInt())
            background = roundedBackground(0xEEFFFFFF.toInt())
            setPadding(dp(16), dp(10), dp(16), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
        }
    }

    private fun makeButton(label: String): Button {
        return Button(this).apply {
            text = label
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            background = roundedBackground(0xFFFF4D88.toInt())
            setPadding(dp(16), dp(10), dp(16), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            ).apply {
                setMargins(0, dp(8), 0, dp(8))
            }
        }
    }

    private fun roundedBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(18).toFloat()
        }
    }

    private fun createAndShareHtml(title: String, question: String, finalMessage: String) {
        try {
            val photoUri = selectedImageUri
            if (photoUri == null) {
                Toast.makeText(this, "Choisis d’abord une photo 😉", Toast.LENGTH_LONG).show()
                return
            }

            val imageBase64 = convertImageToBase64(photoUri)
            val html = generateHtml(title, question, finalMessage, imageBase64)

            val outputFile = File(cacheDir, "ouvre_moi.html")
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

            startActivity(Intent.createChooser(shareIntent, "Envoyer avec"))

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp n'est pas installé 😢", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val maxSize = 1400

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: throw IllegalArgumentException("Impossible de lire l'image")

        val largestSide = max(options.outWidth, options.outHeight)
        val sampleSize = max(1, (largestSide / maxSize.toFloat()).roundToInt())

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }

        val bitmap = contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
        } ?: throw IllegalArgumentException("Impossible de décoder l'image")

        val scaledBitmap = scaleBitmapIfNeeded(bitmap, maxSize)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 82, outputStream)

        if (scaledBitmap !== bitmap) {
            bitmap.recycle()
            scaledBitmap.recycle()
        } else {
            bitmap.recycle()
        }

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val largestSide = max(width, height)

        if (largestSide <= maxSize) {
            return bitmap
        }

        val ratio = maxSize.toFloat() / largestSide.toFloat()
        val newWidth = (width * ratio).roundToInt()
        val newHeight = (height * ratio).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun generateHtml(
        title: String,
        question: String,
        finalMessage: String,
        imageBase64: String
    ): String {
        val safeTitle = escapeHtml(title)
        val safeQuestion = escapeHtml(question)
        val safeFinalMessage = escapeHtml(finalMessage)

        return """
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Une petite question ❤️</title>
  <style>
    * { box-sizing: border-box; }

    body {
      margin: 0;
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: "Segoe UI", Arial, sans-serif;
      background: linear-gradient(135deg, #ffdde1, #ee9ca7);
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      overflow: hidden;
      transition: background-image 0.8s ease;
    }

    body.photo-active {
      background-image:
        linear-gradient(rgba(255, 180, 210, 0.12), rgba(120, 20, 70, 0.12)),
        url("data:image/jpeg;base64,$imageBase64");
    }

    body.photo-active::before {
      content: "";
      position: fixed;
      inset: 0;
      background: rgba(255, 120, 170, 0.18);
      backdrop-filter: blur(1px);
      z-index: 0;
      pointer-events: none;
    }

    .card {
      width: min(92vw, 520px);
      padding: 36px 26px;
      text-align: center;
      background: rgba(255, 255, 255, 0.88);
      border-radius: 28px;
      box-shadow: 0 20px 60px rgba(120, 30, 70, 0.25);
      position: relative;
      z-index: 2;
    }

    .card.photo-reveal {
      background: transparent;
      box-shadow: none;
      pointer-events: none;
    }

    .card.photo-reveal .buttons { display: none; }

    .card.photo-reveal h1,
    .card.photo-reveal p,
    .card.photo-reveal .message {
      display: block;
      text-shadow: 0 3px 12px rgba(0, 0, 0, 0.55);
    }

    .card.photo-reveal h1,
    .card.photo-reveal p { color: white; }

    .card.photo-reveal .message {
      background: rgba(0, 0, 0, 0.20);
      color: white;
      backdrop-filter: blur(2px);
    }

    h1 {
      margin: 0 0 14px;
      color: #d6336c;
      font-size: clamp(2rem, 7vw, 3.2rem);
      line-height: 1.1;
    }

    p {
      margin: 0 0 30px;
      color: #633044;
      font-size: clamp(1.1rem, 4vw, 1.35rem);
    }

    .buttons {
      position: relative;
      min-height: 150px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 22px;
      padding: 0 42px;
    }

    button {
      border: none;
      border-radius: 999px;
      padding: 16px 32px;
      font-size: 1.25rem;
      font-weight: 700;
      cursor: pointer;
      transition: transform 0.22s ease, box-shadow 0.22s ease, left 0.22s ease, top 0.22s ease;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.16);
    }

    #yesBtn {
      background: #ff4d88;
      color: white;
      transform: scale(1);
      z-index: 3;
      margin-right: auto;
    }

    #noBtn {
      background: white;
      color: #d6336c;
      border: 2px solid #ff9fbd;
      position: absolute;
      right: 42px;
      top: 50%;
      transform: translateY(-50%);
      z-index: 4;
    }

    .message {
      display: none;
      margin-top: 24px;
      padding: 18px;
      border-radius: 18px;
      background: rgba(255, 255, 255, 0.22);
      color: white;
      backdrop-filter: blur(2px);
      font-size: 1.25rem;
      font-weight: 700;
    }

    .heart {
      position: fixed;
      top: -30px;
      color: rgba(255, 255, 255, 0.75);
      font-size: 28px;
      animation: fall linear infinite;
      z-index: 1;
      pointer-events: none;
    }

    @keyframes fall {
      to { transform: translateY(110vh) rotate(360deg); }
    }

    @media (max-width: 480px) {
      .buttons {
        min-height: 170px;
        padding: 0 24px;
      }

      #noBtn { right: 24px; }

      button {
        padding: 14px 24px;
        font-size: 1.1rem;
      }
    }
  </style>
</head>
<body>
  <main class="card">
    <h1>$safeTitle</h1>
    <p>$safeQuestion</p>

    <div class="buttons" id="buttonsBox">
      <button id="yesBtn">Oui 💖</button>
      <button id="noBtn">Non 😢</button>
    </div>

    <div class="message" id="message">$safeFinalMessage</div>
  </main>

  <script>
    const noBtn = document.getElementById("noBtn");
    const yesBtn = document.getElementById("yesBtn");
    const message = document.getElementById("message");
    const buttonsBox = document.getElementById("buttonsBox");

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
    });

    function createHeart() {
      const heart = document.createElement("div");
      heart.className = "heart";
      heart.textContent = "❤";
      heart.style.left = Math.random() * 100 + "vw";
      heart.style.animationDuration = 4 + Math.random() * 4 + "s";
      heart.style.fontSize = 18 + Math.random() * 28 + "px";
      document.body.appendChild(heart);
      setTimeout(() => heart.remove(), 8000);
    }

    setInterval(createHeart, 350);
  </script>
</body>
</html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#039;")
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }
}
