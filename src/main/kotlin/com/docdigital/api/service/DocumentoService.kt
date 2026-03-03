package com.docdigital.api.service

import com.docdigital.api.model.Documento
import com.docdigital.api.model.Usuario
import com.docdigital.api.repository.DocumentoRepository
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
open class DocumentoService(
    private val documentoRepository: DocumentoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    fun cadastrarPorEmail(documento: Documento, email: String): Documento {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        documento.usuario = usuario

        return documentoRepository.save(documento)
    }

    fun listarPorEmail(email: String): List<Documento> {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        return documentoRepository.findByUsuario(usuario)
    }
}