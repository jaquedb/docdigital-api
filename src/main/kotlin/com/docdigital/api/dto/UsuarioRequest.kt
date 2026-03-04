package com.docdigital.api.dto

data class UsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String
)