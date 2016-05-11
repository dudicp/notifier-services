package com.patimer.notifier.dto;

public class PredicateDtoBuilder
{
    private Integer minPrice = 1300000;
    private Integer maxPrice = 200000;
    private SellerTypeDto sellerType = SellerTypeDto.Private;

    public PredicateDtoBuilder withMinPrice(Integer minPrice)
    {
        this.minPrice = minPrice;
        return this;
    }

    public PredicateDtoBuilder withMaxPrice(Integer maxPrice)
    {
        this.maxPrice = maxPrice;
        return this;
    }

    public PredicateDtoBuilder withSellerType(SellerTypeDto sellerType)
    {
        this.sellerType = sellerType;
        return this;
    }

    public PredicateDto build()
    {
        return new PredicateDto(minPrice, maxPrice, sellerType);
    }
}
