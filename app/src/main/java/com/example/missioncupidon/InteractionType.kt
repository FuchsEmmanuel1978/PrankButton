package com.example.missioncupidon

data class InteractionType(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val needsSecondaryButton: Boolean,
    val needsPhoto: Boolean = true,
    val isAdvanced: Boolean = false
)

object InteractionTypeCatalog {

    fun all(): List<InteractionType> = listOf(
        InteractionType(
            id = "simple_reveal",
            name = "Simple reveal",
            emoji = "✨",
            description = "La personne clique et découvre la photo avec le message.",
            needsSecondaryButton = false
        ),
        InteractionType(
            id = "trap_button",
            name = "Bouton piège",
            emoji = "😈",
            description = "Le mauvais bouton fuit, le bon bouton devient irrésistible.",
            needsSecondaryButton = true
        ),
        InteractionType(
            id = "photo_puzzle",
            name = "Puzzle photo",
            emoji = "🧩",
            description = "La personne débloque le message en reconstituant la photo.",
            needsSecondaryButton = false,
            isAdvanced = true
        ),
        InteractionType(
            id = "mystery_gift",
            name = "Cadeau mystère",
            emoji = "🎁",
            description = "La personne ouvre des cadeaux jusqu’à trouver la surprise.",
            needsSecondaryButton = false
        ),
        InteractionType(
            id = "catch_emoji",
            name = "Attrape emoji",
            emoji = "😂",
            description = "La personne attrape un emoji plusieurs fois pour révéler le message.",
            needsSecondaryButton = false,
            isAdvanced = true
        ),
        InteractionType(
            id = "animated_message",
            name = "Message animé",
            emoji = "💌",
            description = "Le message apparaît progressivement, comme une lettre magique.",
            needsSecondaryButton = false
        )
    )

    fun find(id: String): InteractionType =
        all().firstOrNull { it.id == id } ?: all().first()

    fun recommendedFor(category: MessageCategory): List<InteractionType> {
        val recommended = category.recommendedInteractionIds.map { find(it) }
        val others = all().filterNot { item -> recommended.any { it.id == item.id } }
        return recommended + others
    }
}
