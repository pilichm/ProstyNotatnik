package pl.pilichm.prostynotatnik.recyclerview

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val noteText: String = "",
    val noteCreationTime: String = ""
)
