package kz.aspan.data.models

import kz.aspan.other.Constants.TYPE_CHAT_MESSAGE

data class ChatMessage(
    val from: String,
    val roomName: String,
    val message: String,
    val timeStamp: Long
) : BaseModel(TYPE_CHAT_MESSAGE)
