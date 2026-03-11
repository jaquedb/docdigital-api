package com.docdigital.api.controller

import com.docdigital.api.dto.AlertaDocumentosResponse
import com.docdigital.api.dto.DocumentoResponse
import com.docdigital.api.model.CategoriaDocumento
import com.docdigital.api.model.Documento
import com.docdigital.api.service.DocumentoService
import com.docdigital.api.service.FileStorageService
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/documentos")
class DocumentoController(
    private val documentoService: DocumentoService,
    private val fileStorageService: FileStorageService
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun cadastrar(
        @RequestParam("file") file: MultipartFile,
        @RequestParam nome: String,
        @RequestParam(required = false) descricao: String?,
        @RequestParam categoria: CategoriaDocumento,
        @RequestParam(required = false) dataVencimento: String?
    ): ResponseEntity<DocumentoResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val nomeArquivo = fileStorageService.salvarArquivo(file)

        val documento = Documento(
            nome = nome,
            descricao = descricao,
            categoria = categoria,
            caminhoArquivo = nomeArquivo,
            tipoArquivo = file.contentType ?: "application/octet-stream",
            dataVencimento = dataVencimento?.let { LocalDate.parse(it) }
        )

        val documentoSalvo = documentoService.cadastrarPorEmail(documento, email)

        val response = DocumentoResponse.from(
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

    // NOVO ENDPOINT PARA MULTIPÁGINA
    @PostMapping("/multipage", consumes = ["multipart/form-data"])
    fun cadastrarMultipage(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam nome: String,
        @RequestParam(required = false) descricao: String?,
        @RequestParam categoria: CategoriaDocumento,
        @RequestParam(required = false) dataVencimento: String?
    ): ResponseEntity<DocumentoResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        if (files.isEmpty()) {
            throw IllegalArgumentException("Nenhum arquivo enviado")
        }

        val nomeArquivo = fileStorageService.salvarArquivosMultipage(files)

        val documento = Documento(
            nome = nome,
            descricao = descricao,
            categoria = categoria,
            caminhoArquivo = nomeArquivo,
            tipoArquivo = "application/pdf",
            dataVencimento = dataVencimento?.let { LocalDate.parse(it) }
        )

        val documentoSalvo = documentoService.cadastrarPorEmail(documento, email)

        val response = DocumentoResponse.from(
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

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody request: com.docdigital.api.dto.DocumentoRequest
    ): ResponseEntity<DocumentoResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val documentoAtualizado = documentoService.atualizarPorIdEEmail(id, request, email)

        val response = DocumentoResponse.from(
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

    @GetMapping("/download/{nomeArquivo}")
    fun downloadArquivo(@PathVariable nomeArquivo: String): ResponseEntity<UrlResource> {

        val caminhoArquivo: Path = fileStorageService.carregarArquivo(nomeArquivo)

        val resource = UrlResource(caminhoArquivo.toUri())

        val contentType = Files.probeContentType(caminhoArquivo)?.let {
            MediaType.parseMediaType(it)
        } ?: MediaType.APPLICATION_OCTET_STREAM

        return ResponseEntity.ok()
            .contentType(contentType)
            .contentLength(Files.size(caminhoArquivo))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${resource.filename}\""
            )
            .body(resource)
    }

    @GetMapping("/visualizar/{nomeArquivo}")
    fun visualizarArquivo(@PathVariable nomeArquivo: String): ResponseEntity<UrlResource> {

        val caminhoArquivo: Path = fileStorageService.carregarArquivo(nomeArquivo)

        val resource = UrlResource(caminhoArquivo.toUri())

        val contentType = when {
            nomeArquivo.endsWith(".png", true) -> MediaType.IMAGE_PNG
            nomeArquivo.endsWith(".jpg", true) -> MediaType.IMAGE_JPEG
            nomeArquivo.endsWith(".jpeg", true) -> MediaType.IMAGE_JPEG
            nomeArquivo.endsWith(".pdf", true) -> MediaType.APPLICATION_PDF
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        return ResponseEntity.ok()
            .contentType(contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${resource.filename}\"")
            .body(resource)
    }

    @GetMapping("/buscar")
    fun buscarPorPalavraChave(
        @RequestParam palavra: String
    ): ResponseEntity<List<DocumentoResponse>> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val documentos = documentoService.buscarPorPalavraChave(email, palavra)

        val response = documentos.map {
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

        return ResponseEntity.ok(response)
    }

    @GetMapping("/alertas")
    fun verificarAlertas(): ResponseEntity<AlertaDocumentosResponse> {

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Usuário não autenticado")

        val email = authentication.name

        val alertas = documentoService.verificarAlertas(email)

        return ResponseEntity.ok(alertas)
    }
}