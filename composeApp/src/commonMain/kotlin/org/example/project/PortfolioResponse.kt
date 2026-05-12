package org.example.project

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    val url: String,
    val html: String
)
