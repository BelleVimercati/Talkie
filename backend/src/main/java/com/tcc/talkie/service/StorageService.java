package com.tcc.talkie.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/* Guarda as imagens das ocorrências em disco, em app.upload-dir */
@Slf4j
@Service
public class StorageService {

    public static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private final Path uploadDir;

    public StorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /* Valida e salva a imagem; devolve o nome do arquivo gerado (nunca o nome enviado pelo usuário) */
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Envie uma imagem");
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BadRequestException("A imagem deve ter no máximo 5 MB");
        }

        try {
            // O tipo informado pelo navegador pode ser falsificado: confere a assinatura do arquivo
            String extension = detectImageExtension(file);
            String filename = UUID.randomUUID() + extension;
            Files.createDirectories(uploadDir);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar a imagem", e);
        }
    }

    public byte[] load(String filename) {
        try {
            Path path = resolve(filename);
            if (!Files.exists(path)) {
                throw new NotFoundException("Imagem não encontrada");
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler a imagem", e);
        }
    }

    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(resolve(filename));
        } catch (IOException e) {
            // Arquivo órfão não impede a operação; fica registrado para limpeza manual
            log.warn("Não foi possível apagar a imagem {}", filename, e);
        }
    }

    public String contentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    /* Impede que um nome de arquivo saia do diretório de uploads (ex.: "../") */
    private Path resolve(String filename) {
        Path path = uploadDir.resolve(filename).normalize();
        if (!path.startsWith(uploadDir)) {
            throw new BadRequestException("Arquivo inválido");
        }
        return path;
    }

    private String detectImageExtension(MultipartFile file) throws IOException {
        byte[] header;
        try (InputStream in = file.getInputStream()) {
            header = in.readNBytes(12);
        }
        if (startsWith(header, 0xFF, 0xD8, 0xFF)) return ".jpg";
        if (startsWith(header, 0x89, 'P', 'N', 'G')) return ".png";
        if (header.length >= 12 && startsWith(header, 'R', 'I', 'F', 'F')
            && new String(Arrays.copyOfRange(header, 8, 12)).equals("WEBP")) return ".webp";
        throw new BadRequestException("Formato de imagem não suportado (use JPEG, PNG ou WebP)");
    }

    private boolean startsWith(byte[] data, int... signature) {
        if (data.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if ((data[i] & 0xFF) != signature[i]) return false;
        }
        return true;
    }
}
