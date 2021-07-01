package kz.aspan.data.models

import kz.aspan.data.Room
import kz.aspan.other.Constants.TYPE_PHASE_CHANGE

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    val drawingPlayer: String? = null
) : BaseModel(TYPE_PHASE_CHANGE)
