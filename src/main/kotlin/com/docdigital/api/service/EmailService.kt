package com.docdigital.api.service

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    fun enviarEmail(destinatario: String, assunto: String, mensagemHtml: String) {

        val mensagem: MimeMessage = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(mensagem, true, "UTF-8")

        helper.setFrom("DocDigital <docdigital.app@gmail.com>")
        helper.setTo(destinatario)
        helper.setSubject(assunto)
        helper.setText(mensagemHtml, true)

        mailSender.send(mensagem)
    }
}