package com.patimer.notifier.model;

import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;

import java.util.*;

public class SearcherStoredDataEntityBuilder
{
    private UUID searcherId = UUID.randomUUID();
    private Date lastSearchTime = Calendar.getInstance().getTime();
    private ItemType itemType = ItemType.Apartment;
    private List<SearchedItem> storedItems = new ArrayList<>();

    public SearcherStoredDataEntityBuilder()
    {
        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().withLink("link1").build();
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().withLink("link2").build();
        storedItems.add(apartmentItem1);
        storedItems.add(apartmentItem2);
    }

    public SearcherStoredDataEntityBuilder withSearcherId(UUID searcherId)
    {
        this.searcherId = searcherId;
        return this;
    }

    public SearcherStoredDataEntityBuilder withLastSearchTime(Date lastSearchTime)
    {
        this.lastSearchTime = lastSearchTime;
        return this;
    }

    public SearcherStoredDataEntityBuilder withItemType(ItemType itemType)
    {
        this.itemType = itemType;
        return this;
    }

    public SearcherStoredDataEntityBuilder withStoredItems(List<SearchedItem> storedItems)
    {
        this.storedItems = storedItems;
        return this;
    }

    public SearcherStoredDataEntity build()
    {
        return new SearcherStoredDataEntity(searcherId, lastSearchTime, itemType, storedItems);
    }
}
