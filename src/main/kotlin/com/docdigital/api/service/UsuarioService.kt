package com.docdigital.api.service

import com.docdigital.api.model.Usuario
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val authService: AuthService
) {

    fun cadastrar(usuario: Usuario): Usuario {

        val emailNormalizado = usuario.email.lowercase()
        usuario.email = emailNormalizado

        val senhaOriginal = usuario.senha
            ?: throw IllegalArgumentException("Senha é obrigatória")

        if (senhaOriginal.length != 6 || !senhaOriginal.all { it.isDigit() }) {
            throw IllegalArgumentException("A senha deve conter exatamente 6 números")
        }

        if (usuarioRepository.findByEmail(usuario.email).isPresent) {
            throw IllegalArgumentException("Email já cadastrado")
        }

        val senhaCriptografada = passwordEncoder.encode(senhaOriginal)
            ?: throw RuntimeException("Erro ao criptografar senha")

        usuario.senha = senhaCriptografada

        return usuarioRepository.save(usuario)
    }

    fun cadastrarComVerificacao(
        nome: String,
        email: String,
        senha: String
    ): Usuario {

        val usuario = Usuario(
            nome = nome,
            email = email,
            senha = senha
        )

        val usuarioSalvo = cadastrar(usuario)

        val codigo = authService.gerarCodigo()

        authService.salvarCodigoCadastro(usuarioSalvo.email, codigo)

        val mensagemHtml = gerarTemplateEmail(
            "Confirmação de cadastro 📄",
            codigo,
            "Digite esse código no app para ativar sua conta."
        )

        emailService.enviarEmail(
            usuarioSalvo.email,
            "Confirmação de cadastro - DocDigital",
            mensagemHtml
        )

        return usuarioSalvo
    }

    fun atualizarFcmToken(usuarioId: Long, token: String) {
        val usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        usuario.fcmToken = token

        usuarioRepository.save(usuario)
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
                <h1 style="font-size: 36px; letter-spacing: 4px; margin: 20px 0;">
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