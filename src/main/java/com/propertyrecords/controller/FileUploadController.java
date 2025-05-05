package com.propertyrecords.controller;

import com.propertyrecords.model.Owner;
import com.propertyrecords.model.Property;
import com.propertyrecords.repository.OwnerRepository;
import com.propertyrecords.repository.PropertyRepository;
import com.propertyrecords.service.PdfExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class FileUploadController {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PdfExtractionService pdfExtractionService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Construct full absolute path
            String rootPath = System.getProperty("user.dir"); // e.g., C:/Users/benpa/dev/propertyrecords
            String uploadDirPath = rootPath + File.separator + "updatedPdfs";
            File uploadDir = new File(uploadDirPath);

            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("Created upload dir: " + created);
            }

            File destination = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(destination);

            System.out.println("Saved PDF to: " + destination.getAbsolutePath());

            // Extract and persist data
            Property property = pdfExtractionService.extractPropertyFromPdf(destination);
            Owner savedOwner = ownerRepository.save(property.getOwner());
            property.setOwner(savedOwner);

            propertyRepository.save(property);

            return ResponseEntity.ok("File uploaded and data saved!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to process file.");
        }
    }

}
