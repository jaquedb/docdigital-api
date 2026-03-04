package com.docdigital.api.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class FileStorageService(

    @Value("\${file.upload-dir}")
    private val uploadDir: String

) {

    fun salvarArquivo(file: MultipartFile): String {

        val uploadPath: Path = Paths.get(uploadDir)

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val nomeOriginal = file.originalFilename ?: "arquivo"
        val nomeUnico = UUID.randomUUID().toString() + "_" + nomeOriginal

        val caminhoArquivo = uploadPath.resolve(nomeUnico)

        Files.copy(file.inputStream, caminhoArquivo)

        return nomeUnico
    }

    fun carregarArquivo(nomeArquivo: String): Path {

        val uploadPath: Path = Paths.get(uploadDir)

        val caminhoArquivo = uploadPath.resolve(nomeArquivo)

        if (!Files.exists(caminhoArquivo)) {
            throw IllegalArgumentException("Arquivo não encontrado")
        }

        return caminhoArquivo
    }
}