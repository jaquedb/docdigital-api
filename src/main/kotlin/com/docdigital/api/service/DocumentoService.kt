package com.docdigital.api.service

import com.docdigital.api.dto.DocumentoRequest
import com.docdigital.api.model.Documento
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
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        documento.usuario = usuario

        return documentoRepository.save(documento)
    }

    fun listarPorEmail(email: String): List<Documento> {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        return documentoRepository.findByUsuario(usuario)
    }

    fun deletarPorIdEEmail(id: Long, email: String) {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val documento = documentoRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Documento não encontrado") }

        if (documento.usuario.id != usuario.id) {
            throw IllegalArgumentException("Você não tem permissão para deletar este documento")
        }

        documentoRepository.delete(documento)
    }

    fun atualizarPorIdEEmail(id: Long, request: DocumentoRequest, email: String): Documento {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val documento = documentoRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Documento não encontrado") }

        if (documento.usuario.id != usuario.id) {
            throw IllegalArgumentException("Você não tem permissão para editar este documento")
        }

        documento.nome = request.nome
        documento.descricao = request.descricao
        documento.categoria = request.categoria
        documento.caminhoArquivo = request.caminhoArquivo
        documento.tipoArquivo = request.tipoArquivo
        documento.dataVencimento = request.dataVencimento

        return documentoRepository.save(documento)
    }

    // NOVO MÉTODO DE BUSCA POR PALAVRA-CHAVE
    fun buscarPorPalavraChave(email: String, palavra: String): List<Documento> {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        return documentoRepository
            .findByUsuarioAndNomeContainingIgnoreCaseOrUsuarioAndDescricaoContainingIgnoreCase(
                usuario,
                palavra,
                usuario,
                palavra
            )
    }
}