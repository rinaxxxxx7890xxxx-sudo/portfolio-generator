package org.example.project

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val name: String = "",
    val role: String = "",
    val iconUrl: String = "",
    val bio: String = "",
    val skills: String = "",
    val projects: String = "",
    val experience: String = "",
    val github: String = "",
    val zenn: String = "",
    val email: String = ""
)
