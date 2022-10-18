package sopro.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import sopro.model.Action;
import sopro.model.AttachedFile;
import sopro.repository.ActionRepository;
import sopro.repository.AttachedFileRepository;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.TransactionRepository;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class AttachedFileService implements AttachedFileInterface {

    @Autowired
    AttachedFileRepository attachedFileRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public AttachedFile storeFile(MultipartFile file) { //TODO handle exceptions properly.
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new IllegalArgumentException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            AttachedFile dbFile = new AttachedFile(fileName, file.getContentType(), file.getBytes());

            return attachedFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public AttachedFile getFile(long fileId) {
        return attachedFileRepository.findById(fileId).orElse(null); //TODO Handle exception properly.
    }

    public ByteArrayOutputStream generatePdfFromAction(long actionId) {
        long[] arr = { actionId };

        try {
            return buildPdf(arr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public ByteArrayOutputStream generatePdfFromTransaction(long transactionId) {
        List<Action> ax = transactionRepository.findById(transactionId).get().getActions();
        long[] arr = new long[ax.size()];
        for (int i = 0; i < ax.size(); ++i)
            arr[i] = ax.get(i).getId();

        try {

            return buildPdf(arr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private ByteArrayOutputStream buildPdf(long[] actionIdList) throws DocumentException, IOException {

        Font font8 = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);
        Font font10 = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK);
        Font font14 = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        long lastActionId = actionIdList[actionIdList.length - 1];

        for (long actionId : actionIdList) {
            // ab hier content
            Action a = actionRepository.findById(actionId).get();

            String[] body = {
                    "Initiator: " + a.getInitiator().getCompany().getName(),
                    "Receiver: " + (a.getTransaction().getBuyer() == a.getInitiator().getCompany()
                            ? a.getTransaction().getSeller().getName()
                            : a.getTransaction().getBuyer().getName()),
                    " ",
                    "Action: " + a.getActiontype().getName() + ": " + a.getActiontype().getText(),
                    " ",
                    "Amount: " + a.getAmount(),
                    "Price per Piece: " + a.getPricePerPiece(),
                    "Message: " + a.getMessage()
            };
            // header

            //TODO: get from repository!!
            Image img = Image.getInstance(System.getProperty("user.dir") + "/build/resources/main/static/img/placeholder.jpg");

            try {
                img = Image.getInstance(a.getInitiator().getCompany().getCompanyLogo().getLogo());
            } catch (Exception e) {

            }

            img.scaleAbsolute(50, 50);
            document.add(img);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss, dd.MM.uuuu");
            Paragraph p = new Paragraph(dtf.format(LocalDateTime.now()), font10);
            Paragraph init = new Paragraph(a.getInitiator().getCompany().getName(), font10);
            init.setAlignment(Element.ALIGN_RIGHT);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(init);
            document.add(p);

            // Horizantal line
            LineSeparator ls = new LineSeparator();
            document.add(new Chunk(ls));
            // Footnotes
            PdfPTable footer = new PdfPTable(3);
            // set defaults
            footer.setWidths(new int[] { 24, 2, 1 });
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.addCell(new Phrase(a.getInitiator().getCompany().getDescription(), font8));
            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d", writer.getPageNumber()), font8));
            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell();
            totalPageCount.setBorder(Rectangle.TOP);
            footer.addCell(totalPageCount);
            // write page
            PdfContentByte canvas = writer.getDirectContent();
            footer.writeSelectedRows(0, -1, 34, 50, canvas);

            for (String elem : body) {
                if (elem.contains("null")) // skip empty variables.
                    continue;

                p = new Paragraph(elem, font14);
                p.setAlignment(Element.ALIGN_LEFT);
                document.add(p);
            }

            if (actionId != lastActionId)
                document.newPage();

        }
        document.close();
        return out;
    }
}
