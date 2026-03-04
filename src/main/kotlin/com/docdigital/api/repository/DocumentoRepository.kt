package com.docdigital.api.repository

import com.docdigital.api.model.Documento
import com.docdigital.api.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentoRepository : JpaRepository<Documento, Long> {

    fun findByUsuario(usuario: Usuario): List<Documento>

    fun findByUsuarioAndNomeContainingIgnoreCaseOrUsuarioAndDescricaoContainingIgnoreCase(
        usuario: Usuario,
        nome: String,
        usuario2: Usuario,
        descricao: String
    ): List<Documento>
}