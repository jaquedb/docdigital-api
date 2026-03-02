package com.docdigital.api.service

import com.docdigital.api.model.Usuario
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository
) {

    fun cadastrar(usuario: Usuario): Usuario {

        if (usuarioRepository.findByEmail(usuario.email).isPresent) {
            throw RuntimeException("Email já cadastrado")
        }

        return usuarioRepository.save(usuario)
    }

}