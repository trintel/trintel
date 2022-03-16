package sopro.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.Action;
import sopro.repository.ActionRepository;
import sopro.repository.CompanyLogoRepository;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

@Service
public class PdfService implements PdfInterface {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    public String generatePdf(long actionId) {
        String path = "pdf-export-" + actionId + ".pdf";

        try {
            buildPdf(actionId, path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;

    }

    private void buildPdf(long actionId, String path) throws DocumentException, IOException {

        Action a = actionRepository.findById(actionId).get();

        String[] body = {
                "Initiator: " + a.getInitiator().getCompany().getName(),
                "Receiver: " + (a.getTransaction().getBuyer() == a.getInitiator().getCompany() ? a.getTransaction().getSeller().getName() :a.getTransaction().getBuyer().getName()),
                "Action: " + a.getActiontype().getName() + ": " + a.getActiontype().getText(),
                " ",
                "Amount: " + a.getAmount(),
                "Price / Piece: " + a.getPricePerPiece()
                // TODO restliche Felder.
        };

        String FONT = "./src/main/resources/font/Segoe_UI.ttf";
        BaseFont bf = BaseFont.createFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font10 = new Font(bf, 10);
        Font font14 = new Font(bf, 14);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        // header

        Image img = Image.getInstance("./src/main/resources/static/img/placeholder.jpg");

        try {
            img = Image.getInstance(a.getInitiator().getCompany().getCompanyLogo().getLogo());
        } catch (Exception e) {

        }

        img.scaleAbsolute(50, 50);
        document.add(img);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss, dd.MM.uuuu");
        Paragraph p = new Paragraph(dtf.format(LocalDateTime.now()), font10);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);

        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));

        for (String elem : body) {
            p = new Paragraph(elem, font14);
            p.setAlignment(Element.ALIGN_LEFT);
            document.add(p);
        }

        document.close();
    }

    // public void generateTransactionPdf(Transaction transaction) {

    // for (Action action : transaction.getActions()) {
    // try {
    // buildPdf(action.getId());

    // } catch (Exception e) {
    // // TODO: handle exception
    // }
    // }

    // }

}
