package com.docdigital.api.controller

import com.docdigital.api.config.JwtService
import com.docdigital.api.dto.LoginRequest
import com.docdigital.api.dto.LoginResponse
import com.docdigital.api.repository.UsuarioRepository
import com.docdigital.api.service.EmailService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import com.docdigital.api.service.AuthService

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/auth")
class AuthController(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val emailService: EmailService,
    private val authService: AuthService
) {

    // Armazena códigos de recuperação em memória
    private val codigosRecuperacao = mutableMapOf<String, Pair<String, Long>>()

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {

        val emailNormalizado = request.email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        //  BLOQUEIO DE LOGIN
        if (!usuario.emailVerificado) {
            throw IllegalArgumentException("Email não verificado. Verifique seu email antes de fazer login.")
        }

        val senhaValida = passwordEncoder.matches(request.senha, usuario.senha)

        if (!senhaValida) {
            throw IllegalArgumentException("Senha inválida")
        }

        val token = jwtService.generateToken(usuario.email)

        return ResponseEntity.ok(LoginResponse(token, usuario.nome))
    }

    // Gera código de recuperação
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestParam email: String): ResponseEntity<Map<String, String>> {

        val emailNormalizado = email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Email não encontrado") }

        val codigo = (100000..999999).random().toString()

        codigosRecuperacao[emailNormalizado] =
            Pair(codigo, System.currentTimeMillis())

        val mensagemHtml = """
            <div style="font-family: Arial; text-align: center;">
                <h2 style="color: #4CAF50;">Recuperação de senha</h2>
                <p>Olá,</p>
                <p>Seu código de recuperação é:</p>
                <h1 style="font-size: 40px; color: #000;">$codigo</h1>
                <p>Este código expira em 10 minutos.</p>
                <hr>
                <p style="font-size: 12px; color: gray;">
                    Se você não solicitou isso, ignore este email.
                </p>
            </div>
        """.trimIndent()

        emailService.enviarEmail(
            emailNormalizado,
            "Recuperação de senha - DocDigital",
            mensagemHtml
        )

        return ResponseEntity.ok(
            mapOf("mensagem" to "Código enviado para o email")
        )
    }

    // Redefinir senha
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam email: String,
        @RequestParam codigo: String,
        @RequestParam novaSenha: String
    ): ResponseEntity<Map<String, String>> {

        val emailNormalizado = email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Email não encontrado") }

        val dadosCodigo = codigosRecuperacao[emailNormalizado]
            ?: throw IllegalArgumentException("Nenhum código solicitado para este email")

        val codigoSalvo = dadosCodigo.first
        val tempoGerado = dadosCodigo.second
        val agora = System.currentTimeMillis()

        val expirou = agora - tempoGerado > 600000

        if (expirou) {
            codigosRecuperacao.remove(emailNormalizado)
            throw IllegalArgumentException("Código expirado")
        }

        if (codigoSalvo != codigo) {
            throw IllegalArgumentException("Código inválido")
        }

        if (!novaSenha.matches(Regex("^\\d{6}$"))) {
            throw IllegalArgumentException("A senha deve ter exatamente 6 números")
        }

        usuario.senha = passwordEncoder.encode(novaSenha)!!
        usuarioRepository.save(usuario)

        codigosRecuperacao.remove(emailNormalizado)

        return ResponseEntity.ok(
            mapOf("mensagem" to "Senha redefinida com sucesso")
        )
    }

    @PostMapping("/confirmar-cadastro")
    fun confirmarCadastro(
        @RequestParam email: String,
        @RequestParam codigo: String
    ): ResponseEntity<Map<String, String>> {

        val emailNormalizado = email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val dadosCodigo = authService.obterCodigoCadastro(emailNormalizado)
            ?: throw IllegalArgumentException("Nenhum código encontrado para este email")

        val codigoSalvo = dadosCodigo.first
        val tempoGerado = dadosCodigo.second

        val agora = System.currentTimeMillis()
        val expirou = agora - tempoGerado > 600000

        if (expirou) {
            authService.removerCodigoCadastro(emailNormalizado)
            throw IllegalArgumentException("Código expirado")
        }

        if (codigoSalvo != codigo) {
            throw IllegalArgumentException("Código inválido")
        }

        //  ATIVA USUÁRIO
        usuario.emailVerificado = true
        usuarioRepository.save(usuario)

        authService.removerCodigoCadastro(emailNormalizado)

        return ResponseEntity.ok(
            mapOf("mensagem" to "Cadastro confirmado com sucesso")
        )
    }
}