package com.patimer.notifier.model;

import java.util.Map;

public class PredicateEntity
{
    private Integer minPrice;
    private Integer maxPrice;
    private SellerType sellerType;
    private Map<String, String> additionalPredicates;

    protected PredicateEntity(){} // default constructor required by mongodb

    public PredicateEntity(Integer minPrice, Integer maxPrice, SellerType sellerType, Map<String, String> additionalPredicates)
    {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sellerType = sellerType;
        this.additionalPredicates = additionalPredicates;
    }

    public PredicateEntity(PredicateEntity other)
    {
        this.minPrice = other.getMinPrice();
        this.maxPrice = other.getMaxPrice();
        this.sellerType = other.getSellerType();
        this.additionalPredicates = other.getAdditionalPredicates();
    }

    public Integer getMinPrice()
    {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice)
    {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice()
    {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice)
    {
        this.maxPrice = maxPrice;
    }

    public SellerType getSellerType()
    {
        return sellerType;
    }

    public void setSellerType(SellerType sellerType)
    {
        this.sellerType = sellerType;
    }

    public Map<String, String> getAdditionalPredicates()
    {
        return additionalPredicates;
    }

    public void setAdditionalPredicates(Map<String, String> additionalPredicates)
    {
        this.additionalPredicates = additionalPredicates;
    }
}
