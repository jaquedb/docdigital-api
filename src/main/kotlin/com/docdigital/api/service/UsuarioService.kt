package com.docdigital.api.service

import com.docdigital.api.model.Usuario
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun cadastrar(usuario: Usuario): Usuario {

        val senhaOriginal = usuario.senha
            ?: throw IllegalArgumentException("Senha é obrigatória")

        if (usuarioRepository.findByEmail(usuario.email).isPresent) {
            throw IllegalArgumentException("Email já cadastrado")
        }


        val senhaCriptografada = passwordEncoder.encode(senhaOriginal)
            ?: throw RuntimeException("Erro ao criptografar senha")

        usuario.senha = senhaCriptografada


        return usuarioRepository.save(usuario)
    }
}