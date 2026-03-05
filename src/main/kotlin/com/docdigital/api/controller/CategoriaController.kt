package com.docdigital.api.controller

import com.docdigital.api.model.CategoriaDocumento
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categorias")
class CategoriaController {

    @GetMapping
    fun listarCategorias(): ResponseEntity<List<String>> {

        val categorias = CategoriaDocumento.values().map { it.name }

        return ResponseEntity.ok(categorias)
    }
}