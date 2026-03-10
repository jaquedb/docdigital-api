package com.docdigital.api.dto

import com.docdigital.api.model.CategoriaDocumento
import java.time.LocalDate

data class DocumentoRequest(
    val nome: String,
    val descricao: String?,
    val categoria: CategoriaDocumento,
    val dataVencimento: LocalDate?
)