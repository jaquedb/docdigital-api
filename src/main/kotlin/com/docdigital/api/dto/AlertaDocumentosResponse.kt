package com.docdigital.api.dto

data class AlertaDocumentosResponse(

    val vencidos: List<DocumentoResponse>,

    val venceHoje: List<DocumentoResponse>,

    val venceEm5Dias: List<DocumentoResponse>,

    val venceEm10Dias: List<DocumentoResponse>

)