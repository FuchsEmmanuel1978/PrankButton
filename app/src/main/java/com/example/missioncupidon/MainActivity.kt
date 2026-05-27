package com.example.missioncupidon

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : ComponentActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var imagePreview: ImageView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imagePreview.setImageURI(uri)
        } else {
            Toast.makeText(this, "Aucune photo sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

    data class PrankTheme(
        val name: String,
        val emoji: String,
        val title: String,
        val question: String,
        val yesText: String,
        val noText: String,
        val finalMessage: String
    )

    private val themes = listOf(
        PrankTheme(
            name = "Amour",
            emoji = "❤️",
            title = "Ma chérie ❤️",
            question = "Est-ce que tu m’aimes ?",
            yesText = "Oui 💖",
            noText = "Non 😢",
            finalMessage = "Je le savais 😍 Moi aussi je t’aime fort ❤️"
        ),
        PrankTheme(
            name = "Apéro",
            emoji = "🍻",
            title = "Question importante 🍻",
            question = "Est-ce l’heure de l’apéro ?",
            yesText = "Oui 🍻",
            noText = "Non 😱",
            finalMessage = "Bonne réponse 😄 Santé ! 🍻"
        ),
        PrankTheme(
            name = "Fan",
            emoji = "⚽",
            title = "Question de supporter ⚽",
            question = "Quelle est la meilleure équipe ?",
            yesText = "La mienne 🔥",
            noText = "L’autre 😬",
            finalMessage = "Voilà, enfin quelqu’un de lucide 😄⚽"
        ),
        PrankTheme(
            name = "Famille",
            emoji = "👨‍👩‍👧‍👦",
            title = "Question familiale 👨‍👩‍👧‍👦",
            question = "Qui est le plus drôle de la famille ?",
            yesText = "Toi 😄",
            noText = "Pas toi 😅",
            finalMessage = "Je savais que tu dirais la vérité 😄"
        ),
        PrankTheme(
            name = "Amis",
            emoji = "😎",
            title = "Question entre amis 😎",
            question = "Tu reconnais que je suis le plus fort ?",
            yesText = "Oui 😎",
            noText = "Jamais 😂",
            finalMessage = "Merci, c’est noté officiellement 😎"
        ),
        PrankTheme(
            name = "Anniversaire",
            emoji = "🎂",
            title = "Surprise 🎂",
            question = "Tu veux ton cadeau maintenant ?",
            yesText = "Oui 🎁",
            noText = "Non 😭",
            finalMessage = "Trop tard, la surprise arrive 🎉🎂"
        ),
        PrankTheme(
            name = "Travail",
            emoji = "💼",
            title = "Question professionnelle 💼",
            question = "Est-ce que cette réunion aurait pu être un email ?",
            yesText = "Oui 📧",
            noText = "Non 💼",
            finalMessage = "Enfin quelqu’un de raisonnable 😄📧"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showThemeSelection()
    }

    private fun showThemeSelection() {
        selectedImageUri = null

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 60, 36, 36)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "Prank Button 😄"
            textSize = 34f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Choisis un thème pour ta page piège"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 36)
        }

        layout.addView(title)
        layout.addView(subtitle)

        themes.forEach { theme ->
            val button = Button(this).apply {
                text = "${theme.emoji} ${theme.name}"
                textSize = 20f
                setPadding(16, 18, 16, 18)
                setOnClickListener {
                    showCreationScreen(theme)
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 18)
            }

            layout.addView(button, params)
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun showCreationScreen(theme: PrankTheme) {
        val scrollView = ScrollView(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 50, 36, 36)
        }

        val backButton = Button(this).apply {
            text = "← Changer de thème"
            setOnClickListener {
                showThemeSelection()
            }
        }

        val titleApp = TextView(this).apply {
            text = "${theme.emoji} ${theme.name}"
            textSize = 30f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 24)
        }

        val titleInput = EditText(this).apply {
            hint = "Titre"
            setText(theme.title)
        }

        val questionInput = EditText(this).apply {
            hint = "Question"
            setText(theme.question)
        }

        val yesInput = EditText(this).apply {
            hint = "Texte du bouton positif"
            setText(theme.yesText)
        }

        val noInput = EditText(this).apply {
            hint = "Texte du bouton qui fuit"
            setText(theme.noText)
        }

        val finalMessageInput = EditText(this).apply {
            hint = "Message après le clic"
            setText(theme.finalMessage)
        }

        imagePreview = ImageView(this).apply {
            adjustViewBounds = true
            maxHeight = 420
            setPadding(0, 24, 0, 24)
            setImageResource(android.R.drawable.ic_menu_gallery)
        }

        val choosePhotoButton = Button(this).apply {
            text = "Choisir une photo 📸"
            setOnClickListener {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }

        val sendButton = Button(this).apply {
            text = "Créer et envoyer sur WhatsApp 🚀"
            textSize = 20f
            setOnClickListener {
                val title = titleInput.text.toString().ifBlank { theme.title }
                val question = questionInput.text.toString().ifBlank { theme.question }
                val yesText = yesInput.text.toString().ifBlank { theme.yesText }
                val noText = noInput.text.toString().ifBlank { theme.noText }
                val finalMessage = finalMessageInput.text.toString().ifBlank { theme.finalMessage }

                createAndShareHtml(
                    title = title,
                    question = question,
                    yesText = yesText,
                    noText = noText,
                    finalMessage = finalMessage
                )
            }
        }

        layout.addView(backButton)
        layout.addView(titleApp)
        layout.addView(titleInput)
        layout.addView(questionInput)
        layout.addView(yesInput)
        layout.addView(noInput)
        layout.addView(finalMessageInput)
        layout.addView(imagePreview)
        layout.addView(choosePhotoButton)
        layout.addView(sendButton)

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun createAndShareHtml(
        title: String,
        question: String,
        yesText: String,
        noText: String,
        finalMessage: String
    ) {
        try {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Choisis d’abord une photo 😉", Toast.LENGTH_LONG).show()
                return
            }

            val imageBase64 = convertImageToBase64(selectedImageUri!!)
            val html = generateHtml(
                title = title,
                question = question,
                yesText = yesText,
                noText = noText,
                finalMessage = finalMessage,
                imageBase64 = imageBase64
            )

            val outputFile = File(cacheDir, "prank_button.html")
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
            Toast.makeText(this, "WhatsApp n'est pas installé 😢", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
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

        return """
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Prank Button 😄</title>
  <style>
    * {
      box-sizing: border-box;
    }

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

    .card.photo-reveal .buttons {
      display: none;
    }

    .card.photo-reveal h1,
    .card.photo-reveal p,
    .card.photo-reveal .message {
      display: block;
      text-shadow: 0 3px 12px rgba(0, 0, 0, 0.55);
    }

    .card.photo-reveal h1,
    .card.photo-reveal p {
      color: white;
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
      min-height: 140px;
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
      to {
        transform: translateY(110vh) rotate(360deg);
      }
    }
  </style>
</head>
<body>
  <main class="card">
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
}