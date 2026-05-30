package com.example.missioncupidon

data class ThemeColors(
    val appStart: String,
    val appEnd: String,
    val accent: String,
    val pageStart: String,
    val pageEnd: String,
    val pageAccent: String
)

data class CategoryDefaults(
    val title: String,
    val question: String,
    val primaryButton: String,
    val secondaryButton: String,
    val finalMessage: String
)

data class MessageCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val defaults: CategoryDefaults,
    val colors: ThemeColors,
    val fallingSymbols: List<String>,
    val finalEmoji: String,
    val recommendedInteractionIds: List<String>
)

object MessageCategoryCatalog {

    fun all(): List<MessageCategory> = listOf(
        MessageCategory(
            id = "laugh",
            name = "Faire rire",
            emoji = "😂",
            defaults = CategoryDefaults(
                title = "J’ai une question très sérieuse 😂",
                question = "Tu es prêt à rigoler ?",
                primaryButton = "Oui 😂",
                secondaryButton = "Même pas 😐",
                finalMessage = "Mission réussie : sourire obligatoire 😂"
            ),
            colors = ThemeColors("#F97316", "#EC4899", "#EA580C", "#f97316", "#ec4899", "#ea580c"),
            fallingSymbols = listOf("😂", "🤣", "😄", "✨", "🎉"),
            finalEmoji = "😂",
            recommendedInteractionIds = listOf("trap_button", "catch_emoji", "mystery_gift")
        ),
        MessageCategory(
            id = "love",
            name = "Dire je t’aime",
            emoji = "❤️",
            defaults = CategoryDefaults(
                title = "Juste pour toi ❤️",
                question = "Tu sais que je t’aime ?",
                primaryButton = "Oui 💖",
                secondaryButton = "Pas sûr 😢",
                finalMessage = "Je t’aime fort, vraiment ❤️"
            ),
            colors = ThemeColors("#FF5F9E", "#A855F7", "#FF3D81", "#ff5f9e", "#a855f7", "#ff3d81"),
            fallingSymbols = listOf("❤️", "💖", "💕", "💘", "✨"),
            finalEmoji = "💖",
            recommendedInteractionIds = listOf("animated_message", "simple_reveal", "mystery_gift")
        ),
        MessageCategory(
            id = "birthday",
            name = "Souhaiter un anniversaire",
            emoji = "🎂",
            defaults = CategoryDefaults(
                title = "Joyeux anniversaire 🎂",
                question = "Tu veux ouvrir ta surprise ?",
                primaryButton = "Ouvrir 🎁",
                secondaryButton = "Pas encore",
                finalMessage = "Joyeux anniversaire ! Que ta journée soit magique 🎉"
            ),
            colors = ThemeColors("#EC4899", "#F97316", "#DB2777", "#ec4899", "#f97316", "#db2777"),
            fallingSymbols = listOf("🎂", "🎉", "🎁", "🥳", "✨"),
            finalEmoji = "🎉",
            recommendedInteractionIds = listOf("mystery_gift", "photo_puzzle", "simple_reveal")
        ),
        MessageCategory(
            id = "thanks",
            name = "Dire merci",
            emoji = "🙏",
            defaults = CategoryDefaults(
                title = "Un grand merci 🙏",
                question = "Tu sais à quel point je suis reconnaissant ?",
                primaryButton = "Découvrir 🙏",
                secondaryButton = "Plus tard",
                finalMessage = "Merci pour tout. Tu comptes beaucoup pour moi 🙏"
            ),
            colors = ThemeColors("#14B8A6", "#22C55E", "#0F766E", "#14b8a6", "#22c55e", "#0f766e"),
            fallingSymbols = listOf("🙏", "💛", "✨", "🌟", "💐"),
            finalEmoji = "🙏",
            recommendedInteractionIds = listOf("simple_reveal", "animated_message", "mystery_gift")
        ),
        MessageCategory(
            id = "comfort",
            name = "Réconforter",
            emoji = "🤗",
            defaults = CategoryDefaults(
                title = "Un petit câlin virtuel 🤗",
                question = "Tu veux recevoir un peu de douceur ?",
                primaryButton = "Oui 🤗",
                secondaryButton = "Pas maintenant",
                finalMessage = "Je pense à toi. Tu n’es pas seul ou seule 🤗"
            ),
            colors = ThemeColors("#60A5FA", "#A78BFA", "#2563EB", "#60a5fa", "#a78bfa", "#2563eb"),
            fallingSymbols = listOf("🤗", "💙", "☁️", "✨", "🌙"),
            finalEmoji = "🤗",
            recommendedInteractionIds = listOf("animated_message", "simple_reveal", "mystery_gift")
        ),
        MessageCategory(
            id = "sorry",
            name = "S’excuser",
            emoji = "😢",
            defaults = CategoryDefaults(
                title = "Je suis désolé 😢",
                question = "Tu acceptes de lire mon message ?",
                primaryButton = "Oui 🤍",
                secondaryButton = "Je boude encore",
                finalMessage = "Je suis sincèrement désolé. Tu comptes beaucoup pour moi 🤍"
            ),
            colors = ThemeColors("#94A3B8", "#38BDF8", "#64748B", "#94a3b8", "#38bdf8", "#64748b"),
            fallingSymbols = listOf("😢", "🤍", "🌧️", "🕊️", "✨"),
            finalEmoji = "🤍",
            recommendedInteractionIds = listOf("animated_message", "simple_reveal", "trap_button")
        ),
        MessageCategory(
            id = "motivate",
            name = "Motiver",
            emoji = "🔥",
            defaults = CategoryDefaults(
                title = "Tu vas y arriver 🔥",
                question = "Tu veux un boost d’énergie ?",
                primaryButton = "Go 🔥",
                secondaryButton = "Pas là",
                finalMessage = "Tu es capable de grandes choses. Ne lâche rien 🔥"
            ),
            colors = ThemeColors("#EF4444", "#F59E0B", "#DC2626", "#ef4444", "#f59e0b", "#dc2626"),
            fallingSymbols = listOf("🔥", "⚡", "🏆", "💪", "🚀"),
            finalEmoji = "🔥",
            recommendedInteractionIds = listOf("catch_emoji", "simple_reveal", "animated_message")
        ),
        MessageCategory(
            id = "surprise",
            name = "Annoncer une surprise",
            emoji = "🎁",
            defaults = CategoryDefaults(
                title = "J’ai une surprise 🎁",
                question = "Tu veux la découvrir ?",
                primaryButton = "Oui 🎁",
                secondaryButton = "Non",
                finalMessage = "Surprise ! J’espère que ça va te faire sourire 🎁"
            ),
            colors = ThemeColors("#8B5CF6", "#EC4899", "#7C3AED", "#8b5cf6", "#ec4899", "#7c3aed"),
            fallingSymbols = listOf("🎁", "✨", "🎉", "💫", "😮"),
            finalEmoji = "🎁",
            recommendedInteractionIds = listOf("mystery_gift", "photo_puzzle", "simple_reveal")
        ),
        MessageCategory(
            id = "declaration",
            name = "Faire une déclaration",
            emoji = "💌",
            defaults = CategoryDefaults(
                title = "J’ai quelque chose à te dire 💌",
                question = "Tu veux lire mon message ?",
                primaryButton = "Oui 💌",
                secondaryButton = "Plus tard",
                finalMessage = "Voilà ce que je voulais te dire : tu es important pour moi 💌"
            ),
            colors = ThemeColors("#F472B6", "#8B5CF6", "#DB2777", "#f472b6", "#8b5cf6", "#db2777"),
            fallingSymbols = listOf("💌", "❤️", "🌹", "✨", "💫"),
            finalEmoji = "💌",
            recommendedInteractionIds = listOf("animated_message", "simple_reveal", "mystery_gift")
        ),
        MessageCategory(
            id = "prank",
            name = "Faire un prank",
            emoji = "😈",
            defaults = CategoryDefaults(
                title = "Question piège 😈",
                question = "Tu penses pouvoir cliquer sur la mauvaise réponse ?",
                primaryButton = "Bonne réponse 😄",
                secondaryButton = "Mauvaise réponse 😈",
                finalMessage = "Trop facile ! Tu t’es fait piéger 😈"
            ),
            colors = ThemeColors("#7C3AED", "#111827", "#A855F7", "#7c3aed", "#111827", "#a855f7"),
            fallingSymbols = listOf("😈", "😂", "💥", "🌀", "🎭"),
            finalEmoji = "😈",
            recommendedInteractionIds = listOf("trap_button", "catch_emoji", "mystery_gift")
        )
    )
}
