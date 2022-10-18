package sopro.service;

import java.io.ByteArrayOutputStream;

import org.springframework.web.multipart.MultipartFile;

import sopro.model.AttachedFile;

public interface AttachedFileInterface {
    ByteArrayOutputStream generatePdfFromAction(long actionId);
    ByteArrayOutputStream generatePdfFromTransaction(long transactionId);
    AttachedFile storeFile(MultipartFile file);
}
