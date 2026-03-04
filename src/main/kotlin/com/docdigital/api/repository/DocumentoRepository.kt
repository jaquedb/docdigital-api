package com.docdigital.api.repository

import com.docdigital.api.model.Documento
import com.docdigital.api.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DocumentoRepository : JpaRepository<Documento, Long> {

    fun findByUsuario(usuario: Usuario): List<Documento>

    fun findByUsuarioAndNomeContainingIgnoreCaseOrUsuarioAndDescricaoContainingIgnoreCase(
        usuario: Usuario,
        nome: String,
        usuario2: Usuario,
        descricao: String
    ): List<Documento>

    fun findByUsuarioAndDataVencimentoBefore(
        usuario: Usuario,
        data: LocalDate
    ): List<Documento>

    fun findByUsuarioAndDataVencimento(
        usuario: Usuario,
        data: LocalDate
    ): List<Documento>

    fun findByUsuarioAndDataVencimentoBetween(
        usuario: Usuario,
        inicio: LocalDate,
        fim: LocalDate
    ): List<Documento>
}