package com.docdigital.api.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

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
        val extensao = nomeOriginal.substringAfterLast(".", "").lowercase()

        val nomeUnico = UUID.randomUUID().toString()

        // Se já for PDF salva normalmente
        if (extensao == "pdf") {

            val nomeFinal = "${nomeUnico}.pdf"
            val caminhoArquivo = uploadPath.resolve(nomeFinal)

            Files.copy(file.inputStream, caminhoArquivo)

            return nomeFinal
        }

        // Se for imagem → converter para PDF
        if (extensao == "png" || extensao == "jpg" || extensao == "jpeg") {

            val nomeFinal = "${nomeUnico}.pdf"
            val caminhoArquivo = uploadPath.resolve(nomeFinal)

            val imagem: BufferedImage = ImageIO.read(file.inputStream)

            val documento = PDDocument()
            val pagina = PDPage()

            documento.addPage(pagina)

            val imageObject = PDImageXObject.createFromByteArray(
                documento,
                file.bytes,
                nomeOriginal
            )

            val contentStream = PDPageContentStream(documento, pagina)

            val larguraPagina = pagina.mediaBox.width
            val alturaPagina = pagina.mediaBox.height

            contentStream.drawImage(
                imageObject,
                0f,
                0f,
                larguraPagina,
                alturaPagina
            )

            contentStream.close()

            documento.save(caminhoArquivo.toFile())
            documento.close()

            return nomeFinal
        }

        // Outros arquivos → salvar normalmente
        val nomeFinal = UUID.randomUUID().toString() + "_" + nomeOriginal
        val caminhoArquivo = uploadPath.resolve(nomeFinal)

        Files.copy(file.inputStream, caminhoArquivo)

        return nomeFinal
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