package com.docdigital.api.controller

import com.docdigital.api.dto.DocumentoRequest
import com.docdigital.api.model.Documento
import com.docdigital.api.service.DocumentoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/documentos")
class DocumentoController(
    private val documentoService: DocumentoService
) {

    @PostMapping("/{usuarioId}")
    fun cadastrar(
        @PathVariable usuarioId: Long,
        @RequestBody request: DocumentoRequest
    ): ResponseEntity<Documento> {

        val documento = Documento(
            nome = request.nome,
            descricao = request.descricao,
            categoria = request.categoria,
            caminhoArquivo = request.caminhoArquivo,
            tipoArquivo = request.tipoArquivo,
            dataVencimento = request.dataVencimento
        )

        val documentoSalvo = documentoService.cadastrar(documento, usuarioId)

        return ResponseEntity.ok(documentoSalvo)
    }

    @GetMapping
    fun listarTodos(): ResponseEntity<List<Documento>> {
        val documentos = documentoService.listarTodos()
        return ResponseEntity.ok(documentos)
    }
}