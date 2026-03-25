package com.docdigital.api.controller

import com.docdigital.api.dto.UsuarioRequest
import com.docdigital.api.dto.UsuarioResponse
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

        val usuarioSalvo = usuarioService.cadastrarComVerificacao(
            request.nome,
            request.email,
            request.senha
        )

        val response = UsuarioResponse(
            id = usuarioSalvo.id,
            nome = usuarioSalvo.nome,
            email = usuarioSalvo.email
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/fcm-token")
    fun salvarToken(
        @RequestBody request: Map<String, String>
    ): ResponseEntity<Void> {

        val usuarioId = request["usuarioId"]?.toLong()
            ?: throw IllegalArgumentException("usuarioId é obrigatório")

        val token = request["token"]
            ?: throw IllegalArgumentException("token é obrigatório")

        usuarioService.atualizarFcmToken(usuarioId, token)

        return ResponseEntity.ok().build()
    }
}