package org.example.project

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val name: String,
    val bio: String,
    val github: String,
    val zenn: String
)
