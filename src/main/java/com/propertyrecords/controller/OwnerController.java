package com.propertyrecords.controller;

import com.propertyrecords.model.Owner;
import com.propertyrecords.repository.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "*") // allow Vue.js frontend to access it later
public class OwnerController {

    private final OwnerRepository ownerRepository;

    public OwnerController(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @GetMapping
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @PostMapping
    public Owner createOwner(@RequestBody Owner owner) {
        return ownerRepository.save(owner);
    }

    @GetMapping("/test")
    public String testConnection() {
        return "API is working!";
    }

}
