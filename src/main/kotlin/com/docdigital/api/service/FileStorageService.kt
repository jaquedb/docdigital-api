package com.docdigital.api.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
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

            // LEITURA ORIGINAL
            val imagemOriginal: BufferedImage = ImageIO.read(file.inputStream)

            // MELHORIA DA IMAGEM
            val imagem = melhorarImagem(imagemOriginal)

            val documento = PDDocument()

            val pagina = PDPage(PDRectangle.A4)
            documento.addPage(pagina)

            val imageObject = PDImageXObject.createFromByteArray(
                documento,
                bufferedImageToBytes(imagem),
                nomeOriginal
            )

            val larguraPagina = pagina.mediaBox.width
            val alturaPagina = pagina.mediaBox.height

            val larguraImagem = imageObject.width.toFloat()
            val alturaImagem = imageObject.height.toFloat()

            val escala = minOf(
                larguraPagina / larguraImagem,
                alturaPagina / alturaImagem
            )

            val larguraFinal = larguraImagem * escala
            val alturaFinal = alturaImagem * escala

            val posX = (larguraPagina - larguraFinal) / 2
            val posY = (alturaPagina - alturaFinal) / 2

            val contentStream = PDPageContentStream(documento, pagina)

            contentStream.drawImage(
                imageObject,
                posX,
                posY,
                larguraFinal,
                alturaFinal
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

    private fun melhorarImagem(imagem: BufferedImage): BufferedImage {

        val largura = imagem.width
        val altura = imagem.height

        val imagemNova = BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB)

        val g = imagemNova.createGraphics()

        val escalaContraste = 1.2f
        val brilho = 5f

        val op = java.awt.image.RescaleOp(
            floatArrayOf(escalaContraste, escalaContraste, escalaContraste),
            floatArrayOf(brilho, brilho, brilho),
            null
        )

        val imagemProcessada = op.filter(imagem, null)

        g.drawImage(imagemProcessada, 0, 0, null)
        g.dispose()

        return imagemNova
    }

    private fun bufferedImageToBytes(imagem: BufferedImage): ByteArray {

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(imagem, "jpg", outputStream)

        return outputStream.toByteArray()
    }

    // NOVO MÉTODO PARA PDF MULTIPÁGINA
    fun salvarArquivosMultipage(files: List<MultipartFile>): String {

        val uploadPath: Path = Paths.get(uploadDir)

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val nomeFinal = "${UUID.randomUUID()}.pdf"
        val caminhoArquivo = uploadPath.resolve(nomeFinal)

        val documento = PDDocument()

        files.forEach { file ->

            val imagemOriginal: BufferedImage = ImageIO.read(file.inputStream)
            val imagem = melhorarImagem(imagemOriginal)

            val pagina = PDPage(PDRectangle.A4)
            documento.addPage(pagina)

            val imageObject = PDImageXObject.createFromByteArray(
                documento,
                bufferedImageToBytes(imagem),
                file.originalFilename ?: "pagina"
            )

            val larguraPagina = pagina.mediaBox.width
            val alturaPagina = pagina.mediaBox.height

            val larguraImagem = imageObject.width.toFloat()
            val alturaImagem = imageObject.height.toFloat()

            val escala = minOf(
                larguraPagina / larguraImagem,
                alturaPagina / alturaImagem
            )

            val larguraFinal = larguraImagem * escala
            val alturaFinal = alturaImagem * escala

            val posX = (larguraPagina - larguraFinal) / 2
            val posY = (alturaPagina - alturaFinal) / 2

            val contentStream = PDPageContentStream(documento, pagina)

            contentStream.drawImage(
                imageObject,
                posX,
                posY,
                larguraFinal,
                alturaFinal
            )

            contentStream.close()
        }

        documento.save(caminhoArquivo.toFile())
        documento.close()

        return nomeFinal
    }
}