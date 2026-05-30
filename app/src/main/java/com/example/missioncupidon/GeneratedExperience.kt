package com.example.missioncupidon

import java.util.Locale

data class GeneratedExperience(
    val category: MessageCategory,
    val interaction: InteractionType,
    val title: String,
    val question: String,
    val primaryButton: String,
    val secondaryButton: String,
    val finalMessage: String,
    val imageBase64: String
)

object GeneratedExperienceHtmlGenerator {

    fun generate(experience: GeneratedExperience): String {
        return when (experience.interaction.id) {
            "trap_button" -> generateTrapButton(experience)
            "photo_puzzle" -> generatePhotoPuzzle(experience)
            "mystery_gift" -> generateMysteryGift(experience)
            "catch_emoji" -> generateCatchEmoji(experience)
            "animated_message" -> generateAnimatedMessage(experience)
            else -> generateSimpleReveal(experience)
        }
    }

    private fun generateSimpleReveal(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <button id="mainBtn">${escapeHtml(exp.primaryButton)}</button>
    <div id="message" class="message hidden">${escapeHtml(exp.finalMessage)}</div>
  </main>
            """.trimIndent(),
            script = """
mainBtn.onclick = function() {
  reveal();
  mainBtn.style.display = "none";
};
            """.trimIndent()
        )
    }

    private fun generateTrapButton(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <div class="buttons" id="buttonsBox">
      <button id="yesBtn">${escapeHtml(exp.primaryButton)}</button>
      <button id="noBtn">${escapeHtml(exp.secondaryButton)}</button>
    </div>
    <div id="message" class="message hidden">${escapeHtml(exp.finalMessage)}</div>
  </main>
            """.trimIndent(),
            script = """
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
  } while (Math.hypot(randomX - currentX, randomY - currentY) < minDistance && attempts < 80);

  if (attempts >= 80) {
    randomX = currentX < maxX / 2 ? maxX : 0;
    randomY = Math.random() * maxY;
  }

  noBtn.style.right = "auto";
  noBtn.style.transform = "none";
  noBtn.style.left = randomX + "px";
  noBtn.style.top = randomY + "px";

  yesScale += 0.18;
  yesBtn.style.transform = "scale(" + yesScale + ")";
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

yesBtn.onclick = function() { reveal(); };
            """.trimIndent()
        )
    }

    private fun generatePhotoPuzzle(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <div id="puzzle" class="puzzle-grid"></div>
    <div id="message" class="message hidden">${escapeHtml(exp.finalMessage)}</div>
  </main>
            """.trimIndent(),
            script = """
let next = 1;
for (let i = 1; i <= 9; i++) {
  const tile = document.createElement("button");
  tile.textContent = i;
  tile.className = "puzzle-tile";
  tile.onclick = function() {
    if (i === next) {
      tile.textContent = "📸";
      tile.classList.add("solved");
      next++;
      if (next === 10) { reveal(); }
    } else {
      tile.animate(
        [{ transform: "translateX(0)" }, { transform: "translateX(-8px)" }, { transform: "translateX(8px)" }, { transform: "translateX(0)" }],
        { duration: 250 }
      );
    }
  };
  puzzle.appendChild(tile);
}
            """.trimIndent()
        )
    }

    private fun generateMysteryGift(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <div id="gifts" class="gift-grid"></div>
    <div id="message" class="message hidden">${escapeHtml(exp.finalMessage)}</div>
  </main>
            """.trimIndent(),
            script = """
const winner = Math.floor(Math.random() * 6);
const fakeGifts = ["🍬", "🧸", "🎈", "⭐"];

for (let i = 0; i < 6; i++) {
  const gift = document.createElement("button");
  gift.textContent = "🎁";
  gift.className = "gift-tile";
  gift.onclick = function() {
    if (i === winner) {
      gift.textContent = "✨";
      reveal();
    } else {
      gift.textContent = fakeGifts[Math.floor(Math.random() * fakeGifts.length)];
      gift.disabled = true;
      gift.style.opacity = ".65";
    }
  };
  gifts.appendChild(gift);
}
            """.trimIndent()
        )
    }

    private fun generateCatchEmoji(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <p id="score">Score : 0/5</p>
    <div id="arena" class="arena">
      <button id="emojiBtn" class="emoji-btn">${escapeHtml(exp.category.emoji)}</button>
    </div>
    <div id="message" class="message hidden">${escapeHtml(exp.finalMessage)}</div>
  </main>
            """.trimIndent(),
            script = """
let count = 0;

function moveEmoji() {
  const arenaRect = arena.getBoundingClientRect();
  emojiBtn.style.left = Math.random() * (arenaRect.width - 75) + "px";
  emojiBtn.style.top = Math.random() * (arenaRect.height - 70) + "px";
}

emojiBtn.onclick = function() {
  count++;
  score.textContent = "Score : " + count + "/5";
  if (count >= 5) {
    arena.style.display = "none";
    reveal();
  } else {
    moveEmoji();
  }
};

moveEmoji();
            """.trimIndent()
        )
    }

    private fun generateAnimatedMessage(exp: GeneratedExperience): String {
        return baseHtml(
            exp = exp,
            body = """
  <main class="card">
    ${pill(exp)}
    <h1>${escapeHtml(exp.title)}</h1>
    <p>${escapeHtml(exp.question)}</p>
    <button id="mainBtn">${escapeHtml(exp.primaryButton)}</button>
    <div id="message" class="message hidden"></div>
  </main>
            """.trimIndent(),
            script = """
const finalText = "${escapeJs(exp.finalMessage)}";
let index = 0;

mainBtn.onclick = function() {
  document.body.classList.add("photo-active");
  mainBtn.style.display = "none";
  message.classList.remove("hidden");

  const timer = setInterval(function() {
    message.textContent = finalText.slice(0, index);
    index++;
    if (index > finalText.length) {
      clearInterval(timer);
      rain(24);
    }
  }, 35);
};
            """.trimIndent()
        )
    }

    private fun baseHtml(exp: GeneratedExperience, body: String, script: String): String {
        val symbolsJsArray = exp.category.fallingSymbols.joinToString(prefix = "[", postfix = "]") { "\"${escapeJs(it)}\"" }
        val colors = exp.category.colors
        val htmlLanguage = Locale.getDefault().language.ifBlank { "fr" }

        return """
<!DOCTYPE html>
<html lang="$htmlLanguage">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>MoodPop 💫</title>
  <style>
    * { box-sizing: border-box; }
    body { margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; font-family: "Segoe UI", Arial, sans-serif; background: linear-gradient(135deg, ${colors.pageStart}, ${colors.pageEnd}); background-size: cover; background-position: center; background-repeat: no-repeat; overflow: hidden; transition: background-image 0.8s ease, background 0.8s ease; }
    body.photo-active { background-image: linear-gradient(rgba(0, 0, 0, 0.10), rgba(0, 0, 0, 0.34)), url("data:image/jpeg;base64,${exp.imageBase64}"); }
    body.photo-active::before { content: ""; position: fixed; inset: 0; background: linear-gradient(135deg, rgba(255,255,255,0.10), rgba(0,0,0,0.18)); backdrop-filter: blur(1px); z-index: 0; pointer-events: none; }
    .card { width: min(92vw, 540px); padding: 34px 24px; text-align: center; background: rgba(255, 255, 255, 0.90); border: 1px solid rgba(255,255,255,0.8); border-radius: 30px; box-shadow: 0 22px 70px rgba(0, 0, 0, 0.26); position: relative; z-index: 2; overflow: hidden; }
    .theme-pill { display: inline-block; padding: 8px 14px; border-radius: 999px; background: linear-gradient(135deg, ${colors.pageStart}, ${colors.pageEnd}); color: white; font-weight: 800; margin-bottom: 14px; box-shadow: 0 8px 18px rgba(0,0,0,0.18); }
    .card.photo-reveal { background: transparent; box-shadow: none; border: none; pointer-events: none; }
    .card.photo-reveal .buttons, .card.photo-reveal .theme-pill, .card.photo-reveal .puzzle-grid, .card.photo-reveal .gift-grid, .card.photo-reveal .arena, .card.photo-reveal #score { display: none; }
    .card.photo-reveal h1, .card.photo-reveal p, .card.photo-reveal .message { display: block; text-shadow: 0 4px 16px rgba(0, 0, 0, 0.72); color: white; }
    h1 { margin: 0 0 14px; color: ${colors.pageAccent}; font-size: clamp(2rem, 7vw, 3.2rem); line-height: 1.1; }
    p { margin: 0 0 30px; color: #374151; font-size: clamp(1.1rem, 4vw, 1.35rem); font-weight: 650; }
    button { border: none; border-radius: 999px; padding: 16px 32px; font-size: 1.25rem; font-weight: 900; cursor: pointer; transition: transform 0.22s ease, box-shadow 0.22s ease, left 0.22s ease, top 0.22s ease; box-shadow: 0 12px 24px rgba(0, 0, 0, 0.18); background: linear-gradient(135deg, ${colors.pageStart}, ${colors.pageAccent}); color: white; }
    .buttons { position: relative; min-height: 155px; display: flex; align-items: center; justify-content: space-between; gap: 22px; padding: 0 38px; }
    #yesBtn { z-index: 3; margin-right: auto; }
    #noBtn { background: white; color: ${colors.pageAccent}; border: 3px solid ${colors.pageAccent}; position: absolute; right: 38px; top: 50%; transform: translateY(-50%); z-index: 4; }
    .message { display: none; margin-top: 24px; padding: 18px; border-radius: 20px; background: rgba(255, 255, 255, 0.22); color: white; backdrop-filter: blur(3px); font-size: 1.28rem; font-weight: 900; }
    .puzzle-grid, .gift-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin: 22px 0; }
    .puzzle-tile, .gift-tile { aspect-ratio: 1/1; border-radius: 18px; font-size: 1.8rem; background: rgba(255,255,255,.25); }
    .puzzle-tile.solved { background: linear-gradient(135deg, #ffd1e4, #8ec5ff); color: ${colors.pageAccent}; }
    .gift-tile { font-size: 2.3rem; }
    .arena { position: relative; height: 320px; border-radius: 26px; background: rgba(255,255,255,.18); overflow: hidden; margin-top: 16px; }
    .emoji-btn { position: absolute; font-size: 2rem; padding: 14px 18px; }
    .hidden { display: none; }
    .falling-symbol { position: fixed; top: -40px; color: rgba(255, 255, 255, 0.82); font-size: 28px; animation: fall linear infinite; z-index: 1; pointer-events: none; filter: drop-shadow(0 4px 8px rgba(0,0,0,0.18)); }
    .flash { position: fixed; inset: 0; background: radial-gradient(circle, rgba(255,255,255,0.65), transparent 60%); z-index: 1; animation: flashOut 900ms ease forwards; pointer-events: none; }
    @keyframes fall { to { transform: translateY(112vh) rotate(360deg); } }
    @keyframes flashOut { from { opacity: 1; } to { opacity: 0; } }
  </style>
</head>
<body>
$body
  <script>
    const message = document.getElementById("message");
    const symbols = $symbolsJsArray;
    function reveal() { document.body.classList.add("photo-active"); message.classList.remove("hidden"); document.querySelector(".card").classList.add("photo-reveal"); const flash = document.createElement("div"); flash.className = "flash"; document.body.appendChild(flash); setTimeout(function() { flash.remove(); }, 950); rain(24); }
    function rain(count) { for (let i = 0; i < count; i++) { setTimeout(createFallingSymbol, i * 45); } }
    function createFallingSymbol() { const symbol = document.createElement("div"); symbol.className = "falling-symbol"; symbol.textContent = symbols[Math.floor(Math.random() * symbols.length)]; symbol.style.left = Math.random() * 100 + "vw"; symbol.style.animationDuration = 3.6 + Math.random() * 4.2 + "s"; symbol.style.fontSize = 18 + Math.random() * 30 + "px"; symbol.style.opacity = 0.65 + Math.random() * 0.35; document.body.appendChild(symbol); setTimeout(function() { symbol.remove(); }, 8500); }
    setInterval(createFallingSymbol, 430);
$script
  </script>
</body>
</html>
        """.trimIndent()
    }

    private fun pill(exp: GeneratedExperience): String {
        return """<div class="theme-pill">${escapeHtml(exp.category.finalEmoji)} ${escapeHtml(exp.category.name)} · ${escapeHtml(exp.interaction.emoji)} ${escapeHtml(exp.interaction.name)}</div>"""
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
