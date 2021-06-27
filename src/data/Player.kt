package kz.aspan.data

import io.ktor.http.cio.websocket.WebSocketSession

data class Player(
    val username: String,
    var socket: WebSocketSession,
    val clientId: String,
    var isDrawing: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
)
