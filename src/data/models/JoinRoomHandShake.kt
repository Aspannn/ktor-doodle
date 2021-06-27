package kz.aspan.data.models

import kz.aspan.other.Constants.TYPE_JOIN_ROOM_HANDSHAKE

data class JoinRoomHandShake(
    val username: String,
    val roomName: String,
    val clientId: String
) : BaseModel(TYPE_JOIN_ROOM_HANDSHAKE)