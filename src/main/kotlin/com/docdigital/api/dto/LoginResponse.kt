package com.docdigital.api.dto

data class LoginResponse(
    val token: String,
    val nome: String,
    val id: Long?
)