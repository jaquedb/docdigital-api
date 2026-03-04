package com.docdigital.api.dto

import com.docdigital.api.model.CategoriaDocumento
import java.time.LocalDate
import java.time.LocalDateTime

data class DocumentoResponse(
    val id: Long?,
    val nome: String,
    val descricao: String?,
    val categoria: CategoriaDocumento,
    val dataUpload: LocalDateTime,
    val dataVencimento: LocalDate?,
    val caminhoArquivo: String,
    val tipoArquivo: String
)