package com.docdigital.api.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "documentos")
class Documento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var nome: String,

    var descricao: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    var categoria: CategoriaDocumento,

    @Column(name = "data_upload", nullable = false)
    var dataUpload: LocalDateTime = LocalDateTime.now(),

    @Column(name = "data_vencimento")
    var dataVencimento: LocalDateTime? = null,

    @Column(name = "caminho_arquivo", nullable = false)
    var caminhoArquivo: String,

    @Column(name = "tipo_arquivo", nullable = false)
    var tipoArquivo: String,

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario
)