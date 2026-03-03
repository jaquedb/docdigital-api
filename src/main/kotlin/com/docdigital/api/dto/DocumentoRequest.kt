package com.docdigital.api.dto

import com.docdigital.api.model.CategoriaDocumento
import java.time.LocalDateTime

data class DocumentoRequest(
    val nome: String,
    val descricao: String?,
    val categoria: CategoriaDocumento,
    val caminhoArquivo: String,
    val tipoArquivo: String,
    val dataVencimento: LocalDateTime?
)