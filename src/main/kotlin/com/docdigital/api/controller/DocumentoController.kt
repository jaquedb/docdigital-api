package com.docdigital.api.controller

import com.docdigital.api.dto.DocumentoRequest
import com.docdigital.api.model.Documento
import com.docdigital.api.service.DocumentoService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/documentos")
class DocumentoController(
    private val documentoService: DocumentoService
) {

    @PostMapping
    fun cadastrar(
        @RequestBody request: DocumentoRequest
    ): ResponseEntity<Documento> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("Usuário não autenticado")

        val email = authentication.name

        val documento = Documento(
            nome = request.nome,
            descricao = request.descricao,
            categoria = request.categoria,
            caminhoArquivo = request.caminhoArquivo,
            tipoArquivo = request.tipoArquivo,
            dataVencimento = request.dataVencimento
        )

        val documentoSalvo = documentoService.cadastrarPorEmail(documento, email)

        return ResponseEntity.ok(documentoSalvo)
    }

    @GetMapping
    fun listarTodos(): ResponseEntity<List<Documento>> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("Usuário não autenticado")

        val email = authentication.name
        val documentos = documentoService.listarPorEmail(email)

        return ResponseEntity.ok(documentos)
    }
}