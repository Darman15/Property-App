package com.propertyrecords.model;

import jakarta.persistence.*;

@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    private String address;

    private Double acreage;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    private String acquisitionType;

    private Double lastSoldPrice;

    // Getters and Setters

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAcreage() {
        return acreage;
    }

    public void setAcreage(Double acreage) {
        this.acreage = acreage;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(String acquisitionType) {
        this.acquisitionType = acquisitionType;
    }

    public Double getLastSoldPrice() {
        return lastSoldPrice;
    }

    public void setLastSoldPrice(Double lastSoldPrice) {
        this.lastSoldPrice = lastSoldPrice;
    }
}
