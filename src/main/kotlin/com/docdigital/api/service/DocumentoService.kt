package com.docdigital.api.service

import com.docdigital.api.dto.AlertaDocumentosResponse
import com.docdigital.api.dto.DocumentoRequest
import com.docdigital.api.dto.DocumentoResponse
import com.docdigital.api.model.Documento
import com.docdigital.api.repository.DocumentoRepository
import com.docdigital.api.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
open class DocumentoService(
    private val documentoRepository: DocumentoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val notificacaoService: NotificacaoService
) {

    fun cadastrarPorEmail(documento: Documento, email: String): Documento {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        documento.usuario = usuario

        val docSalvo = documentoRepository.save(documento)

        val token = usuario.fcmToken

        if (token != null) {
            notificacaoService.enviarNotificacao(
                token,
                "Documento cadastrado 📄",
                "Seu documento '${docSalvo.nome}' foi salvo com sucesso"
            )
        }

        return docSalvo
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
        documento.dataVencimento = request.dataVencimento

        return documentoRepository.save(documento)
    }

    // MÉTODO DE BUSCA POR PALAVRA-CHAVE
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

    // MÉTODO DE ALERTAS DE VENCIMENTO
    fun verificarAlertas(email: String): AlertaDocumentosResponse {

        val usuario = usuarioRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val hoje = LocalDate.now()

        val vencidos = documentoRepository
            .findByUsuarioAndDataVencimentoBefore(usuario, hoje)

        val venceHoje = documentoRepository
            .findByUsuarioAndDataVencimento(usuario, hoje)

        val venceEm5Dias = documentoRepository
            .findByUsuarioAndDataVencimentoBetween(
                usuario,
                hoje.plusDays(1),
                hoje.plusDays(5)
            )

        val venceEm10Dias = documentoRepository
            .findByUsuarioAndDataVencimentoBetween(
                usuario,
                hoje.plusDays(6),
                hoje.plusDays(10)
            )

        return AlertaDocumentosResponse(

            vencidos = vencidos.map {
                DocumentoResponse.from(
                    id = it.id,
                    nome = it.nome,
                    descricao = it.descricao,
                    categoria = it.categoria,
                    dataUpload = it.dataUpload,
                    dataVencimento = it.dataVencimento,
                    caminhoArquivo = it.caminhoArquivo,
                    tipoArquivo = it.tipoArquivo
                )
            },

            venceHoje = venceHoje.map {
                DocumentoResponse.from(
                    id = it.id,
                    nome = it.nome,
                    descricao = it.descricao,
                    categoria = it.categoria,
                    dataUpload = it.dataUpload,
                    dataVencimento = it.dataVencimento,
                    caminhoArquivo = it.caminhoArquivo,
                    tipoArquivo = it.tipoArquivo
                )
            },

            venceEm5Dias = venceEm5Dias.map {
                DocumentoResponse.from(
                    id = it.id,
                    nome = it.nome,
                    descricao = it.descricao,
                    categoria = it.categoria,
                    dataUpload = it.dataUpload,
                    dataVencimento = it.dataVencimento,
                    caminhoArquivo = it.caminhoArquivo,
                    tipoArquivo = it.tipoArquivo
                )
            },

            venceEm10Dias = venceEm10Dias.map {
                DocumentoResponse.from(
                    id = it.id,
                    nome = it.nome,
                    descricao = it.descricao,
                    categoria = it.categoria,
                    dataUpload = it.dataUpload,
                    dataVencimento = it.dataVencimento,
                    caminhoArquivo = it.caminhoArquivo,
                    tipoArquivo = it.tipoArquivo
                )
            }
        )
    }
}