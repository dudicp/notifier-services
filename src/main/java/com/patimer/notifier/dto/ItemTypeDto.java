package com.patimer.notifier.dto;

import com.patimer.notifier.model.ItemType;
import org.apache.commons.lang.Validate;

public enum ItemTypeDto
{
    Apartment,
    Motorcycle;

    public static ItemTypeDto fromItemType(ItemType itemType)
    {
        Validate.notNull(itemType);

        switch (itemType)
        {
            case Apartment: return Apartment;
            case Motorcycle: return Motorcycle;
            default: throw new IllegalArgumentException("Unknown type: '" + itemType + "'");
        }
    }

    public ItemType toItemType()
    {
        switch (this)
        {
            case Apartment: return ItemType.Apartment;
            case Motorcycle: return ItemType.Motorcycle;
            default: throw new IllegalArgumentException("Unsupported type: '" + this + "'");
        }
    }
}
