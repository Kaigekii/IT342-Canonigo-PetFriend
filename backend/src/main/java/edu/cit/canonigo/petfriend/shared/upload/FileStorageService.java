package edu.cit.canonigo.petfriend.shared.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public StoredFile storeImage(MultipartFile file, String subfolder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IOException("Only JPG, PNG, or WEBP images are allowed");
        }

        String extension = guessExtension(contentType);
        String safeFolder = (subfolder == null || subfolder.isBlank()) ? "misc" : subfolder.trim();
        Path folderPath = uploadRoot.resolve(safeFolder);
        Files.createDirectories(folderPath);

        String fileName = UUID.randomUUID() + extension;
        Path target = folderPath.resolve(fileName).normalize();
        Files.copy(file.getInputStream(), target);

        String relativePath = safeFolder + "/" + fileName;
        String publicUrl = "/uploads/" + relativePath;

        return new StoredFile(fileName, relativePath, publicUrl, contentType, file.getSize());
    }

    private String guessExtension(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    public static class StoredFile {
        private final String fileName;
        private final String relativePath;
        private final String publicUrl;
        private final String contentType;
        private final long size;

        public StoredFile(String fileName, String relativePath, String publicUrl, String contentType, long size) {
            this.fileName = fileName;
            this.relativePath = relativePath;
            this.publicUrl = publicUrl;
            this.contentType = contentType;
            this.size = size;
        }

        public String getFileName() {
            return fileName;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getPublicUrl() {
            return publicUrl;
        }

        public String getContentType() {
            return contentType;
        }

        public long getSize() {
            return size;
        }
    }
}
