package sopro.service;

import org.springframework.web.multipart.MultipartFile;

import sopro.model.PdfFile;

public interface PdfInterface {
    String generatePdfFromAction(long actionId);
    String generatePdfFromTransaction(long transactionId);
    PdfFile storeFile(MultipartFile file);
}
