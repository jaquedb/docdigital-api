package com.docdigital.api.repository

import com.docdigital.api.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UsuarioRepository : JpaRepository<Usuario, Long> {

    fun findByEmail(email: String): Optional<Usuario>

}