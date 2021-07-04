package kz.aspan.data.models

import kz.aspan.other.Constants.TYPE_PLAYERS_LIST

data class PlayersList(
    val players: List<PlayerData>
) : BaseModel(TYPE_PLAYERS_LIST)
