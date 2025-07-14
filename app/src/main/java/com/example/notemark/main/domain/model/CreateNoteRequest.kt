package com.example.notemark.main.domain.model

import com.example.notemark.main.DateFormatter
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data class CreateNoteRequest(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String = DateFormatter.getCurrentIsoString(),
    @SerialName("lastEditedAt")
    val updatedAt: String = DateFormatter.getCurrentIsoString()
)

// @Serializable(with = UUIDSerializer::class)
//object UUIDSerializer : KSerializer<UUID> {
//
//    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: UUID) {
//        encoder.encodeString(value.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): UUID {
//        return UUID.fromString(decoder.decodeString())
//    }
//}