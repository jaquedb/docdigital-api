package com.docdigital.api.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
class NotificacaoService {

    fun enviarNotificacao(token: String, titulo: String, corpo: String) {

        try {

            val notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo)
                .build()

            val message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build()

            val response = FirebaseMessaging.getInstance().send(message)

            println("✅ Notificação enviada com sucesso!")
            println("Response: $response")

        } catch (e: Exception) {

            println("❌ Erro ao enviar notificação")
            e.printStackTrace()
        }
    }
}