package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SellerType;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.SearchedItem;
import org.apache.commons.lang.Validate;

import java.util.Set;
import java.util.function.Predicate;

public class SellerTypePredicate implements Predicate<SearchedItem>
{
    private Set<SellerType> allowedSellerTypes;
    private boolean allowNullValue;
    private ItemType itemType;

    public SellerTypePredicate(ItemType itemType, Set<SellerType> allowedSellerTypes, boolean allowNullValue)
    {
        Validate.notNull(itemType);
        Validate.notEmpty(allowedSellerTypes);

        this.itemType = itemType;
        this.allowedSellerTypes = allowedSellerTypes;
        this.allowNullValue = allowNullValue;
    }

    @Override
    public boolean test(SearchedItem searchedItem)
    {
        Validate.notNull(searchedItem);

        SellerType sellerType = null;

        if(itemType == ItemType.Apartment)
        {
            sellerType = ((ApartmentItem)searchedItem).getSellerType();
        }
        else
        {
            throw new IllegalArgumentException("Unsupported item type: '" + itemType + "' for predicate: '" + this.getClass() + "'.");
        }

        if(sellerType == null)
        {
            return allowNullValue;
        }
        else
        {
            return (allowedSellerTypes.contains(sellerType));
        }
    }
}
