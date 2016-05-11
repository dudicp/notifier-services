package com.patimer.notifier.dto;

import com.patimer.notifier.model.SellerType;
import org.apache.commons.lang.Validate;

public enum  SellerTypeDto
{
    Private,
    Brokerage;

    public static SellerTypeDto fromSellerType(SellerType sellerType)
    {
        Validate.notNull(sellerType);

        switch (sellerType)
        {
            case Private: return Private;
            case Brokerage: return Brokerage;
            default: throw new IllegalArgumentException("Unknown type: '" + sellerType + "'");
        }
    }

    public SellerType toSellerType()
    {
        switch (this)
        {
            case Private: return SellerType.Private;
            case Brokerage: return SellerType.Brokerage;
            default: throw new IllegalArgumentException("Unsupported type: '" + this + "'");
        }
    }
}
