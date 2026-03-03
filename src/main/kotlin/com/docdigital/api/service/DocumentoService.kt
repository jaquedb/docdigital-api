package com.docdigital.api.service

import com.docdigital.api.model.Documento
import com.docdigital.api.repository.DocumentoRepository
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
open class DocumentoService(
    private val documentoRepository: DocumentoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    fun cadastrar(documento: Documento, usuarioId: Long): Documento {

        val usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        documento.usuario = usuario

        return documentoRepository.save(documento)
    }

    fun listarTodos(): List<Documento> {
        return documentoRepository.findAll()
    }
}