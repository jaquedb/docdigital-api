package com.docdigital.api.repository

import com.docdigital.api.model.Documento
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentoRepository : JpaRepository<Documento, Long>