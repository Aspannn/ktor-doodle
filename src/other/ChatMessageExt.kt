package kz.aspan.other

import io.ktor.util.toLowerCasePreservingASCIIRules
import kz.aspan.data.models.ChatMessage

fun ChatMessage.matchesWord(word: String): Boolean {
    return message.lowercase().trim() == word.lowercase().trim()
}