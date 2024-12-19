package com.javafullstackguru;

import com.javafullstackguru.util.pdf.PDFGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.javafullstackguru"})
public class EmployeeReportApplication {

	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(EmployeeReportApplication.class, args);

		// Ensure PDFGenerator bean is present and correctly configured
		PDFGenerator pdfGenerator = ac.getBean(PDFGenerator.class);
		pdfGenerator.generatePdfReport();
	}
}
