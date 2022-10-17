package sopro.service;

import org.springframework.web.multipart.MultipartFile;

import sopro.model.AttachedFile;

public interface AttachedFileInterface {
    String generatePdfFromAction(long actionId);
    String generatePdfFromTransaction(long transactionId);
    AttachedFile storeFile(MultipartFile file);
}
