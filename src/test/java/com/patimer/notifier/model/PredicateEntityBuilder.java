package com.patimer.notifier.model;

import java.util.HashMap;
import java.util.Map;

public class PredicateEntityBuilder
{
    private Integer minPrice = 1300000;
    private Integer maxPrice = 2000000;
    private SellerType sellerType = SellerType.Private;
    private Map<String, String> additionalPredicates = new HashMap<>();

    public PredicateEntityBuilder withMinPrice(Integer minPrice)
    {
        this.minPrice = minPrice;
        return this;
    }

    public PredicateEntityBuilder withMaxPrice(Integer maxPrice)
    {
        this.maxPrice = maxPrice;
        return this;
    }

    public PredicateEntityBuilder withSellerType(SellerType sellerType)
    {
        this.sellerType = sellerType;
        return this;
    }

    public PredicateEntityBuilder withAdditionalPredicates(Map<String, String> additionalPredicates)
    {
        this.additionalPredicates = additionalPredicates;
        return this;
    }

    public PredicateEntity build()
    {
        return new PredicateEntity(minPrice, maxPrice, sellerType, additionalPredicates);
    }
}
