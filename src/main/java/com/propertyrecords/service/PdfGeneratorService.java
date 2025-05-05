package com.propertyrecords.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.propertyrecords.model.Property;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {

    public void generatePdf(Property property) throws IOException {
        Document document = new Document();
        String fileName = "pdfs/property_" + property.getPropertyId() + ".pdf";

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(new Paragraph("Property Record"));
            document.add(new Paragraph("Address: " + property.getAddress()));
            document.add(new Paragraph("Acreage: " + property.getAcreage()));
            document.add(new Paragraph("Acquisition Type: " + property.getAcquisitionType()));
            document.add(new Paragraph("Last Sold Price: $" + property.getLastSoldPrice()));
            document.add(new Paragraph("Owner: " + property.getOwner().getFirstName() + " " + property.getOwner().getLastName()));
        } catch (DocumentException e) {
            throw new IOException("Failed to create PDF", e);
        } finally {
            document.close();
        }
    }
}
