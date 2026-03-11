package com.docdigital.api.dto

import com.docdigital.api.model.CategoriaDocumento
import java.time.LocalDate
import java.time.LocalDateTime

data class DocumentoResponse(
    val id: Long?,
    val nome: String,
    val descricao: String?,
    val categoria: String,
    val dataUpload: LocalDateTime,
    val dataVencimento: LocalDate?,
    val caminhoArquivo: String,
    val tipoArquivo: String
) {

    companion object {

        fun from(
            id: Long?,
            nome: String,
            descricao: String?,
            categoria: CategoriaDocumento,
            dataUpload: LocalDateTime,
            dataVencimento: LocalDate?,
            caminhoArquivo: String,
            tipoArquivo: String
        ): DocumentoResponse {

            val categoriaFormatada = categoria.name
                .lowercase()
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }

            return DocumentoResponse(
                id,
                nome,
                descricao,
                categoriaFormatada,
                dataUpload,
                dataVencimento,
                caminhoArquivo,
                tipoArquivo
            )
        }
    }
}