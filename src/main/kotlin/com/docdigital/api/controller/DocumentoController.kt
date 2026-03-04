package com.docdigital.api.controller

import com.docdigital.api.dto.DocumentoRequest
import com.docdigital.api.dto.DocumentoResponse
import com.docdigital.api.model.Documento
import com.docdigital.api.service.DocumentoService
import com.docdigital.api.service.FileStorageService
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

@RestController
@RequestMapping("/documentos")
class DocumentoController(
    private val documentoService: DocumentoService,
    private val fileStorageService: FileStorageService
) {

    @PostMapping
    fun cadastrar(
        @RequestBody request: DocumentoRequest
    ): ResponseEntity<DocumentoResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

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

        val response = DocumentoResponse(
            id = documentoSalvo.id,
            nome = documentoSalvo.nome,
            descricao = documentoSalvo.descricao,
            categoria = documentoSalvo.categoria,
            dataUpload = documentoSalvo.dataUpload,
            dataVencimento = documentoSalvo.dataVencimento,
            caminhoArquivo = documentoSalvo.caminhoArquivo,
            tipoArquivo = documentoSalvo.tipoArquivo
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listarTodos(): ResponseEntity<List<DocumentoResponse>> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val documentos = documentoService.listarPorEmail(email)

        val response = documentos.map {
            DocumentoResponse(
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

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody request: DocumentoRequest
    ): ResponseEntity<DocumentoResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val documentoAtualizado = documentoService.atualizarPorIdEEmail(id, request, email)

        val response = DocumentoResponse(
            id = documentoAtualizado.id,
            nome = documentoAtualizado.nome,
            descricao = documentoAtualizado.descricao,
            categoria = documentoAtualizado.categoria,
            dataUpload = documentoAtualizado.dataUpload,
            dataVencimento = documentoAtualizado.dataVencimento,
            caminhoArquivo = documentoAtualizado.caminhoArquivo,
            tipoArquivo = documentoAtualizado.tipoArquivo
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long): ResponseEntity<Void> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        documentoService.deletarPorIdEEmail(id, email)

        return ResponseEntity.noContent().build()
    }

    // ENDPOINT DE UPLOAD
    @PostMapping("/upload")
    fun uploadArquivo(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {

        val nomeArquivo = fileStorageService.salvarArquivo(file)

        return ResponseEntity.ok(
            mapOf("arquivo" to nomeArquivo)
        )
    }

    // NOVO ENDPOINT DE DOWNLOAD
    @GetMapping("/download/{nomeArquivo}")
    fun downloadArquivo(@PathVariable nomeArquivo: String): ResponseEntity<UrlResource> {

        val caminhoArquivo: Path = fileStorageService.carregarArquivo(nomeArquivo)

        val resource = UrlResource(caminhoArquivo.toUri())

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resource.filename}\"")
            .body(resource)
    }
}