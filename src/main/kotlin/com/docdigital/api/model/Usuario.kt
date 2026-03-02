package com.docdigital.api.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "usuarios")
class Usuario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var nome: String,

    @Column(nullable = false, unique = true, length = 150)
    var email: String,

    @Column(nullable = false, length = 255)
    var senha: String,

    @Column(name = "data_criacao", nullable = false)
    var dataCriacao: LocalDateTime = LocalDateTime.now()

)