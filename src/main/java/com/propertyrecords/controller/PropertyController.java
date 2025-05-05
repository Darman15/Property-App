package com.propertyrecords.controller;

import com.propertyrecords.model.Owner;
import com.propertyrecords.model.Property;
import com.propertyrecords.repository.OwnerRepository;
import com.propertyrecords.repository.PropertyRepository;
import com.propertyrecords.service.PdfGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:5173") // adjust this if your frontend runs on a different port
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    // Create new property (with embedded owner)
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        try {
            // Save owner first (because it's required by the property)

//          if Posting by just owner id, must be able to lookup existing owner.

            Owner existingOwner = null;

            if(null != property.getOwner().getOwnerId()) {
                 existingOwner = ownerRepository.findById(property.getOwner().getOwnerId()).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
            }


             if(null != existingOwner) {
                 Owner savedOwner = ownerRepository.save(existingOwner);
                 property.setOwner(savedOwner);
             } else {
                 Owner savedOwner = ownerRepository.save(property.getOwner());
                 property.setOwner(savedOwner);
             }




            // Then save property
            Property savedProperty = propertyRepository.save(property);

            // Generate a PDF for the saved property
            pdfGeneratorService.generatePdf(savedProperty);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProperty);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all properties
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyRepository.findAll());
    }

    // Get property by ID
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete property by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Update property by ID (if needed in the future)
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property updatedProperty) {
        return propertyRepository.findById(id)
                .map(existing -> {
                    existing.setAddress(updatedProperty.getAddress());
                    existing.setAcreage(updatedProperty.getAcreage());
                    existing.setAcquisitionType(updatedProperty.getAcquisitionType());
                    existing.setLastSoldPrice(updatedProperty.getLastSoldPrice());
                    existing.setOwner(updatedProperty.getOwner()); // assume front-end sends full owner object
                    Property saved = propertyRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
