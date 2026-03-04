package com.docdigital.api.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "documentos")
open class Documento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 150)
    var nome: String,

    @Column(columnDefinition = "text")
    var descricao: String? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    var categoria: CategoriaDocumento,

    @Column(name = "data_upload", nullable = false)
    var dataUpload: LocalDateTime = LocalDateTime.now(),

    @Column(name = "data_vencimento")
    var dataVencimento: LocalDate? = null,  // 🔹 ALTERADO AQUI

    @Column(name = "caminho_arquivo", nullable = false, length = 255)
    var caminhoArquivo: String,

    @Column(name = "tipo_arquivo", nullable = false, length = 50)
    var tipoArquivo: String

) {

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    lateinit var usuario: Usuario
}