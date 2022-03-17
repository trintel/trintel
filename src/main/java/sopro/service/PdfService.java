package sopro.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.TrintelApplication;
import sopro.model.Action;
import sopro.repository.ActionRepository;
import sopro.repository.CompanyLogoRepository;
import sopro.repository.TransactionRepository;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class PdfService implements PdfInterface {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public String generatePdfFromAction(long actionId) {
        String path = "pdf-export-" + actionId + ".pdf";
        long[] arr = { actionId };

        try {
            buildPdf(arr, path);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;

    }

    public String generatePdfFromTransaction(long transactionId) {
        String path = "pdf-export-" + transactionId + ".pdf";
        List<Action> ax = transactionRepository.findById(transactionId).get().getActions();
        long[] arr = new long[ax.size()];
        for (int i = 0; i < ax.size(); ++i)
            arr[i] = ax.get(i).getId();

        try {

            buildPdf(arr, path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;

    }

    private void buildPdf(long[] actionIdList, String path) throws DocumentException, IOException {

        String FONT = TrintelApplication.WORKDIR + "/bin/main/font/Segoe_UI.ttf";
        BaseFont bf = BaseFont.createFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font8 = new Font(bf, 8);
        Font font10 = new Font(bf, 10);
        Font font14 = new Font(bf, 14);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
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

            Image img = Image.getInstance(TrintelApplication.WORKDIR + "/bin/main/static/img/placeholder.jpg");

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
    }
}
