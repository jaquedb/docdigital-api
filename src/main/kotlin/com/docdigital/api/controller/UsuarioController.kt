package com.docdigital.api.controller

import com.docdigital.api.model.Usuario
import com.docdigital.api.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @PostMapping
    fun cadastrar(@RequestBody usuario: Usuario): ResponseEntity<Usuario> {
        val usuarioSalvo = usuarioService.cadastrar(usuario)
        return ResponseEntity.ok(usuarioSalvo)
    }
}