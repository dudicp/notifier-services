package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the Data Transfer Object (DTO) of Thing in the system.
 * DTO is used to transfer objects between the UI/External API to the data model.
 */
public class PredicateDto
{
    private Integer minPrice;
    private Integer maxPrice;
    private SellerTypeDto sellerType;

    public PredicateDto(){} // default constructor required by Jackson.

    public PredicateDto(Integer minPrice, Integer maxPrice, SellerTypeDto sellerType)
    {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sellerType = sellerType;
    }

    @JsonProperty("minPrice")
    public Integer getMinPrice()
    {
        return minPrice;
    }

    @JsonProperty("maxPrice")
    public Integer getMaxPrice()
    {
        return maxPrice;
    }

    @JsonProperty("sellerType")
    public SellerTypeDto getSellerType()
    {
        return sellerType;
    }
}
