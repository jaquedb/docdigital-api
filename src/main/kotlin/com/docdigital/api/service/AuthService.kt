package com.docdigital.api.service

import org.springframework.stereotype.Service

@Service
class AuthService {

    // Armazena códigos de confirmação de cadastro
    private val codigosCadastro = mutableMapOf<String, Pair<String, Long>>()

    // GERA CÓDIGO DE 6 DÍGITOS
    fun gerarCodigo(): String {
        return (100000..999999).random().toString()
    }

    // SALVA CÓDIGO COM TIMESTAMP
    fun salvarCodigoCadastro(email: String, codigo: String) {
        codigosCadastro[email] = Pair(codigo, System.currentTimeMillis())
    }

    // BUSCA CÓDIGO
    fun obterCodigoCadastro(email: String): Pair<String, Long>? {
        return codigosCadastro[email]
    }

    // REMOVE CÓDIGO
    fun removerCodigoCadastro(email: String) {
        codigosCadastro.remove(email)
    }

    // VERIFICA SE CÓDIGO EXPIROU (10 minutos)
    fun codigoExpirado(tempoGerado: Long): Boolean {
        val agora = System.currentTimeMillis()
        return agora - tempoGerado > 600000
    }
}