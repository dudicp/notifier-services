package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.SearchedItem;
import org.apache.commons.lang.Validate;

import java.util.function.Predicate;

public class MaxPricePredicate implements Predicate<SearchedItem>
{
    private int max;
    private boolean allowNullValue;
    private ItemType itemType;

    public MaxPricePredicate(ItemType itemType, int max, boolean allowNullValue)
    {
        Validate.notNull(itemType);
        Validate.isTrue(max > 0);

        this.itemType = itemType;
        this.max = max;
        this.allowNullValue = allowNullValue;
    }

    @Override
    public boolean test(SearchedItem searchedItem)
    {
        Validate.notNull(searchedItem);

        Integer price = null;

        if(itemType == ItemType.Apartment)
        {
            price = ((ApartmentItem)searchedItem).getPrice();
        }
        else
        {
            throw new IllegalArgumentException("Unsupported item type: '" + itemType + "' for predicate: '" + this.getClass() + "'.");
        }

        if(price == null)
        {
            return allowNullValue;
        }
        else
        {
            return (price <= max);
        }
    }
}
