package com.docdigital.api.controller

import com.docdigital.api.config.JwtService
import com.docdigital.api.dto.LoginRequest
import com.docdigital.api.dto.LoginResponse
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {

        val usuario = usuarioRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val senhaValida = passwordEncoder.matches(request.senha, usuario.senha)

        if (!senhaValida) {
            throw IllegalArgumentException("Senha inválida")
        }

        val token = jwtService.generateToken(usuario.email)

        return ResponseEntity.ok(LoginResponse(token))
    }
}