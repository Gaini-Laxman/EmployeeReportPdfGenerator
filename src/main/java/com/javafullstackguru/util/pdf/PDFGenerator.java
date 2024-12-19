package com.javafullstackguru.util.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.javafullstackguru.model.Employee;
import com.javafullstackguru.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component("pdfGenerator")
public class PDFGenerator {

    @Value("${pdfDir:D:\\PdfReportRepo\\}")
    private String pdfDir;

    @Value("${reportFileName:Employee-Report}")
    private String reportFileName;

    @Value("${reportFileNameDateFormat:dd_MMMM_yyyy}")
    private String reportFileNameDateFormat;

    @Value("${localDateFormat:yyyy-MM-dd HH:mm:ss}")
    private String localDateFormat;

    @Value("${logoImgPath:}")
    private String logoImgPath;

    @Value("${logoImgScale:100,100}")
    private Float[] logoImgScale;

    @Value("${currencySymbol:₹}")
    private String currencySymbol;

    @Value("${table_noOfColumns:5}")
    private int noOfColumns;

    @Value("${table.columnNames:id,dept,email,name,salary}")
    private String columnNamesString;

    private List<String> columnNames;

    @Autowired
    private EmployeeRepository eRepo;

    private static final Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private static final Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    private static final Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    @PostConstruct
    private void init() {
        // Split columnNamesString into a list of strings
        this.columnNames = List.of(columnNamesString.split(","));
    }

    public void generatePdfReport() {
        Document document = new Document();

        try {
            // Ensure the directory exists
            File directory = new File(pdfDir);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + pdfDir);
            }

            // Create PDF
            PdfWriter.getInstance(document, new FileOutputStream(getPdfNameWithDate()));
            document.open();
            addLogo(document);
            addDocTitle(document);
            createTable(document, noOfColumns);
            addFooter(document);
            System.out.println("------------------Your PDF Report is ready!-------------------------");
        } catch (Exception e) {
            System.err.println("Error while generating the PDF report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private void addLogo(Document document) {
        if (logoImgPath != null && !logoImgPath.isEmpty()) {
            try {
                Image img = Image.getInstance(logoImgPath);
                if (logoImgScale != null && logoImgScale.length == 2) {
                    img.scalePercent(logoImgScale[0], logoImgScale[1]);
                }
                img.setAlignment(Element.ALIGN_RIGHT);
                document.add(img);
            } catch (DocumentException | IOException e) {
                System.err.println("Error adding logo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addDocTitle(Document document) throws DocumentException {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
        Paragraph p1 = new Paragraph();
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph(reportFileName, COURIER));
        p1.setAlignment(Element.ALIGN_CENTER);
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph("Report generated on " + localDateString, COURIER_SMALL));
        document.add(p1);
    }

    private void createTable(Document document, int noOfColumns) throws DocumentException {
        if (columnNames == null || columnNames.size() != noOfColumns) {
            throw new IllegalArgumentException("Number of column names does not match the number of columns.");
        }

        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(noOfColumns);
        table.setWidthPercentage(110);

        for (String columnName : columnNames) {
            PdfPCell cell = new PdfPCell(new Phrase(columnName));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            table.addCell(cell);
        }

        table.setHeaderRows(1);
        getDbData(table);
        document.add(table);
    }

    private void getDbData(PdfPTable table) {
        List<Employee> list = eRepo.getAllEmployeeData();
        for (Employee employee : list) {
            table.addCell(employee.getId() != null ? employee.getId().toString() : "N/A");
            table.addCell(employee.getName() != null ? employee.getName() : "N/A");
            table.addCell(employee.getDept() != null ? employee.getDept() : "N/A");
            table.addCell(employee.getEmail() != null ? employee.getEmail() : "N/A");
            table.addCell(employee.getSalary() != null ? currencySymbol + employee.getSalary().toString() : "N/A");
        }
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph p2 = new Paragraph();
        leaveEmptyLine(p2, 3);
        p2.setAlignment(Element.ALIGN_MIDDLE);
        p2.add(new Paragraph(
                "------------------------End Of " + reportFileName + "------------------------",
                COURIER_SMALL_FOOTER));
        document.add(p2);
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private String getPdfNameWithDate() {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(reportFileNameDateFormat));
        return pdfDir + reportFileName + "-" + localDateString + ".pdf";
    }
}
