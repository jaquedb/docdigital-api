package com.docdigital.api.controller

import com.docdigital.api.dto.UsuarioRequest
import com.docdigital.api.dto.UsuarioResponse
import com.docdigital.api.model.Usuario
import com.docdigital.api.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @PostMapping
    fun cadastrar(@Valid @RequestBody request: UsuarioRequest): ResponseEntity<UsuarioResponse> {

        val usuario = Usuario(
            nome = request.nome,
            email = request.email,
            senha = request.senha
        )

        val usuarioSalvo = usuarioService.cadastrar(usuario)

        val response = UsuarioResponse(
            id = usuarioSalvo.id,
            nome = usuarioSalvo.nome,
            email = usuarioSalvo.email
        )

        return ResponseEntity.ok(response)
    }
}