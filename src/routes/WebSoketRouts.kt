package kz.aspan.routes

import com.google.gson.JsonParser
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.webSocket
import java.lang.Exception
import kotlinx.coroutines.channels.consumeEach
import kz.aspan.data.Player
import kz.aspan.data.Room
import kz.aspan.data.models.Announcement
import kz.aspan.data.models.BaseModel
import kz.aspan.data.models.ChatMessage
import kz.aspan.data.models.DrawData
import kz.aspan.data.models.GameError
import kz.aspan.data.models.JoinRoomHandShake
import kz.aspan.gson
import kz.aspan.other.Constants.TYPE_ANNOUNCEMENT
import kz.aspan.other.Constants.TYPE_CHAT_MESSAGE
import kz.aspan.other.Constants.TYPE_DRAW_DATA
import kz.aspan.other.Constants.TYPE_JOIN_ROOM_HANDSHAKE
import kz.aspan.server
import kz.aspan.session.DrawingSession

fun Route.gameWebSocketRoute() {
    route("/ws/draw") {
        standardWebSocket { socket, clientId, message, payload ->
            when (payload) {
                is JoinRoomHandShake -> {
                    val room = server.rooms[payload.roomName]
                    if (room == null) {
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }
                    val player = Player(
                        payload.username,
                        socket,
                        payload.clientId
                    )
                    server.playerJoined(player)
                    if (!room.containsPlayer(player.username)) {
                        room.addPlayer(player.clientId, player.username, socket)
                    }
                }
                is DrawData -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (room.phase == Room.Phase.GAME_RUNNING) {
                        room.broadcastToAllExcept(message, clientId)
                    }
                }
                is ChatMessage -> {

                }
            }
        }
    }
}

fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
        message: String,
        payload: BaseModel
    ) -> Unit
) {
    webSocket {
        val session = call.sessions.get<DrawingSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val jsonObject = JsonParser.parseString(message).asJsonObject
                    val type = when (jsonObject.get("type").asString) {
                        TYPE_CHAT_MESSAGE -> ChatMessage::class.java
                        TYPE_DRAW_DATA -> DrawData::class.java
                        TYPE_ANNOUNCEMENT -> Announcement::class.java
                        TYPE_JOIN_ROOM_HANDSHAKE -> JoinRoomHandShake::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.clientId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Handle disconnects
        }
    }
}