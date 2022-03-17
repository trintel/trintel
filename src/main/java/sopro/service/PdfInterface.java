package sopro.service;

public interface PdfInterface {
    String generatePdfFromAction(long actionId);
    String generatePdfFromTransaction(long transactionId);
}
