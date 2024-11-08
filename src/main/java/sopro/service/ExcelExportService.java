package sopro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HttpServletBean;

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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class ExcelExportService implements ExcelExportInterface {
     
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
    
    public void excelReport(HttpServletResponse response) {
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

        ArrayList<List<?>> data = new ArrayList<>();
        List<Company> companies = companyRepository.findAll();
        System.out.println(companies);
    
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
         
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }
}
