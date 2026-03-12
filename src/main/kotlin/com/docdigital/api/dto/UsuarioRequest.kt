package com.docdigital.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UsuarioRequest(

    @field:NotBlank
    val nome: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val senha: String
)