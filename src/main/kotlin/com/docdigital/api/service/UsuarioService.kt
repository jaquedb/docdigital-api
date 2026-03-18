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

        val mensagemHtml = """
            <div style="
                font-family: Arial, sans-serif;
                background-color: #0B0F1A;
                padding: 30px;
                text-align: center;
                color: white;
            ">
                <div style="
                    max-width: 400px;
                    margin: auto;
                    background-color: #111827;
                    padding: 30px;
                    border-radius: 12px;
                ">
                    <h2 style="color: #22c55e;">
                        Bem-vindo ao DocDigital 📄
                    </h2>

                    <p style="color: #d1d5db;">
                        Seu código de confirmação é:
                    </p>

                    <h1 style="
                        font-size: 40px;
                        color: #ffffff;
                        letter-spacing: 4px;
                    ">
                        $codigo
                    </h1>

                    <p style="color: #9ca3af;">
                        Digite esse código no app para ativar sua conta.
                    </p>

                    <hr style="margin: 20px 0; border-color: #374151;">

                    <p style="font-size: 12px; color: #6b7280;">
                        Se você não solicitou esse cadastro, ignore este email.
                    </p>
                </div>
            </div>
        """.trimIndent()

        emailService.enviarEmail(
            usuarioSalvo.email,
            "Confirmação de cadastro - DocDigital",
            mensagemHtml
        )

        return usuarioSalvo
    }
}