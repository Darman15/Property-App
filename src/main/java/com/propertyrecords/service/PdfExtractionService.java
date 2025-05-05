package com.propertyrecords.service;

import com.propertyrecords.model.Owner;
import com.propertyrecords.model.Property;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfExtractionService {

    public Property extractPropertyFromPdf(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Basic parsing logic
        String address = extractValue(text, "Address:", "Acreage:");
        double acreage = Double.parseDouble(extractValue(text, "Acreage:", "Acquisition Type:"));
        String acquisition = extractValue(text, "Acquisition Type:", "Last Sold Price:");
        double lastSold = Double.parseDouble(extractValue(text, "Last Sold Price:", "Owner:").replace("$", ""));
        String fullName = extractValue(text, "Owner:", null).trim();

        String[] nameParts = fullName.split(" ");
        String firstName = nameParts.length > 0 ? nameParts[0] : "Unknown";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Create Owner
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);

        // Create Property
        Property property = new Property();
        property.setAddress(address.trim());
        property.setAcreage(acreage);
        property.setAcquisitionType(acquisition.trim());
        property.setLastSoldPrice(lastSold);
        property.setOwner(owner);

        return property;
    }

    private String extractValue(String text, String startLabel, String endLabel) {
        int start = text.indexOf(startLabel);
        if (start == -1) return "";

        start += startLabel.length();
        int end = (endLabel != null) ? text.indexOf(endLabel, start) : text.length();

        if (end == -1 || end < start) return text.substring(start).trim();
        return text.substring(start, end).trim();
    }
}
