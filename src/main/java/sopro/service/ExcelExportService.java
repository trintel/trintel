package sopro.service;

import org.springframework.beans.factory.annotation.Autowired;

import sopro.model.Company;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExportService {
     
    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    StatisticsService statisticsService;

    public static void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    
     
    public void export(File file) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Statistics Report");
         
        Row row = sheet.createRow(0);
         
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        String[] columns = {"Company", "Distinct Buyers", "Distinct Sellers", "Total Transaction Buyer Volume", "Total Transaction Seller Volume", "Number of non confirmed buyer", "Number on non confirmed seller", "Number confirmed"};
         
        for (int i = 0; i < columns.length; i++) {
            createCell(sheet, row, i, columns[i], style);
        }

        ArrayList data = new ArrayList<List>();
        List<Company> companies = companyRepository.findAll();
    
        data.add(companies.stream().map(c -> statisticsService.getNumberDistinctBuyers(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getNumberDistinctSellers(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getTotalTransactionBuyerVolume(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getTotalTransactionSellerVolume(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getNumberNonConfirmedTransactionBuyer(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getNumberNonConfirmedTransactionSeller(c)).collect(Collectors.toList()));
        data.add(companies.stream().map(c -> statisticsService.getNumberConfirmedTransactions(c)).collect(Collectors.toList()));
        int rowCount = 1;
 
        font.setFontHeight(14);
        style.setFont(font);
                 
        // for (Company company : companies) {
        for (int r = 0; r < data.size(); r++) {
            Row row2 = sheet.createRow(rowCount++);
            int columnCount = 0;
             
            for (Object field : data) { 
                createCell(sheet, row2, columnCount++, ((ArrayList) field).get(r), style);
            }
                   
        }
         
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
    }
}
