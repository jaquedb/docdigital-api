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

    private val codigosRecuperacao = mutableMapOf<String, Pair<String, Long>>()

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {

        val emailNormalizado = request.email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val senhaValida = passwordEncoder.matches(request.senha, usuario.senha)

        if (!senhaValida) {
            throw IllegalArgumentException("Senha inválida")
        }

        if (!usuario.emailVerificado) {
            throw IllegalArgumentException("Email não verificado")
        }

        val token = jwtService.generateToken(usuario.email)

        return ResponseEntity.ok(LoginResponse(token, usuario.nome))
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestParam email: String): ResponseEntity<Map<String, String>> {

        val emailNormalizado = email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Email não encontrado") }

        val codigo = authService.gerarCodigo()

        codigosRecuperacao[emailNormalizado] =
            Pair(codigo, System.currentTimeMillis())

        val mensagemHtml = gerarTemplateEmail(
            "Recuperação de senha 🔐",
            codigo,
            "Use esse código para redefinir sua senha no app."
        )

        emailService.enviarEmail(
            emailNormalizado,
            "Recuperação de senha - DocDigital",
            mensagemHtml
        )

        return ResponseEntity.ok(
            mapOf("mensagem" to "Código enviado para o email")
        )
    }

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
            ?: throw IllegalArgumentException("Nenhum código solicitado")

        val codigoSalvo = dadosCodigo.first
        val tempoGerado = dadosCodigo.second

        if (authService.codigoExpirado(tempoGerado)) {
            codigosRecuperacao.remove(emailNormalizado)
            throw IllegalArgumentException("Código expirado")
        }

        if (codigoSalvo != codigo) {
            throw IllegalArgumentException("Código inválido")
        }
        if (novaSenha.length != 6 || !novaSenha.all { it.isDigit() }) {
            throw IllegalArgumentException("A senha deve conter exatamente 6 números")
        }

        usuario.senha = passwordEncoder.encode(novaSenha)!!
        usuarioRepository.save(usuario)

        codigosRecuperacao.remove(emailNormalizado)

        return ResponseEntity.ok(
            mapOf("mensagem" to "Senha redefinida com sucesso")
        )
    }

    @PostMapping("/reenviar-codigo")
    fun reenviarCodigo(@RequestParam email: String): ResponseEntity<Map<String, String>> {

        val emailNormalizado = email.lowercase()

        val usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (usuario.emailVerificado) {
            throw IllegalArgumentException("Email já verificado")
        }

        val codigo = authService.gerarCodigo()

        authService.salvarCodigoCadastro(emailNormalizado, codigo)

        val mensagemHtml = gerarTemplateEmail(
            "Confirmação de cadastro", codigo,
            "Digite esse código no app para ativar sua conta."
        )

        emailService.enviarEmail(
            emailNormalizado,
            "Código de confirmação - DocDigital",
            mensagemHtml
        )

        return ResponseEntity.ok(
            mapOf("mensagem" to "Código reenviado com sucesso")
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
            ?: throw IllegalArgumentException("Nenhum código encontrado")

        val codigoSalvo = dadosCodigo.first
        val tempoGerado = dadosCodigo.second

        if (authService.codigoExpirado(tempoGerado)) {
            authService.removerCodigoCadastro(emailNormalizado)
            throw IllegalArgumentException("Código expirado")
        }

        if (codigoSalvo != codigo) {
            throw IllegalArgumentException("Código inválido")
        }

        usuario.emailVerificado = true
        usuarioRepository.save(usuario)

        authService.removerCodigoCadastro(emailNormalizado)

        return ResponseEntity.ok(
            mapOf("mensagem" to "Cadastro confirmado com sucesso")
        )
    }

    private fun gerarTemplateEmail(
        titulo: String,
        codigo: String,
        mensagem: String
    ): String {

        return """
        <div style="background-color: #0B0F1A; padding: 40px; font-family: Arial; text-align: center;">
            
            <div style="
                max-width: 500px;
                margin: auto;
                background-color: #111827;
                border-radius: 12px;
                padding: 30px;
                color: white;
            ">
                
                <h2 style="color: #22c55e;">$titulo</h2>
                
                <p style="color: #d1d5db;">Seu código é:</p>
                
                <h1 style="
                    font-size: 36px;
                    letter-spacing: 4px;
                    margin: 20px 0;
                ">
                    $codigo
                </h1>
                
                <p style="color: #d1d5db;">
                    $mensagem
                </p>

                <p style="color: #9ca3af; font-size: 12px; margin-top: 20px;">
                    Este código expira em 10 minutos.
                </p>

                <hr style="margin: 20px 0; border-color: #374151;">

                <p style="color: #6b7280; font-size: 12px;">
                    Se você não solicitou isso, ignore este email.
                </p>

            </div>
        </div>
    """.trimIndent()
    }
}