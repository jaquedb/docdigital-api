package com.docdigital.api.service

import com.docdigital.api.repository.DocumentoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class NotificacaoScheduler(
    private val documentoRepository: DocumentoRepository,
    private val notificacaoService: NotificacaoService
) {

    // roda a cada 30 segundos
    @Scheduled(cron = "*/30 * * * * ?")
    fun verificarDocumentos() {

        println("Rodando verificação de documentos...")

        val hoje = LocalDate.now()
        val em3Dias = hoje.plusDays(3)

        val documentos = documentoRepository.findAll()

        for (doc in documentos) {

            val vencimento = doc.dataVencimento ?: continue
            val token = doc.usuario.fcmToken ?: continue

            //  VENCE HOJE
            if (vencimento == hoje && !doc.notificadoHoje) {
                notificacaoService.enviarNotificacao(
                    token,
                    "⚠️ Documento vencendo hoje!",
                    "📄 O documento '${doc.nome}' vence hoje."
                )

                doc.notificadoHoje = true
                documentoRepository.save(doc)
            }

            //  VENCE EM 3 DIAS
            if (vencimento == em3Dias && !doc.notificado3Dias) {
                notificacaoService.enviarNotificacao(
                    token,
                    "⏳ Documento vence em breve",
                    "📄 O documento '${doc.nome}' vence em 3 dias."
                )

                doc.notificado3Dias = true
                documentoRepository.save(doc)
            }
        }
    }
}