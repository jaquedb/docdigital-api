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

    // Armazena códigos de recuperação em memória
    private val codigosRecuperacao = mutableMapOf<String, String>()

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

    // Gera código de recuperação
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestParam email: String): ResponseEntity<Map<String, String>> {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Email não encontrado") }

        // gera código de 6 dígitos
        val codigo = (100000..999999).random().toString()

        codigosRecuperacao[email] = codigo

        return ResponseEntity.ok(
            mapOf("codigo" to codigo)
        )
    }

    // Redefinir senha
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam email: String,
        @RequestParam codigo: String,
        @RequestParam novaSenha: String
    ): ResponseEntity<Map<String, String>> {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Email não encontrado") }

        val codigoSalvo = codigosRecuperacao[email]
            ?: throw IllegalArgumentException("Nenhum código solicitado para este email")

        if (codigoSalvo != codigo) {
            throw IllegalArgumentException("Código inválido")
        }

        // senha deve ter exatamente 6 números
        if (!novaSenha.matches(Regex("^\\d{6}$"))) {
            throw IllegalArgumentException("A senha deve ter exatamente 6 números")
        }

        usuario.senha = passwordEncoder.encode(novaSenha)!!
        usuarioRepository.save(usuario)

        // remove código após uso
        codigosRecuperacao.remove(email)

        return ResponseEntity.ok(
            mapOf("mensagem" to "Senha redefinida com sucesso")
        )
    }
}