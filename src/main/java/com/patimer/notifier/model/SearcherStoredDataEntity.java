package com.patimer.notifier.model;

import com.patimer.notifier.model.item.SearchedItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "searcher_stored_data")
public class SearcherStoredDataEntity
{
    @Id
    private UUID searcherId;

    @NotNull
    private Date lastSearchTime;

    @NotNull
    private ItemType itemType;

    private List<SearchedItem> storedItems;

    protected SearcherStoredDataEntity(){} // default constructor required by mongodb

    public SearcherStoredDataEntity(UUID searcherId, Date lastSearchTime, ItemType itemType, List<SearchedItem> storedItems)
    {
        this.searcherId = searcherId;
        this.lastSearchTime = lastSearchTime;
        this.itemType = itemType;
        this.storedItems = storedItems;
    }

    public UUID getSearcherId()
    {
        return searcherId;
    }

    public void setSearcherId(UUID searcherId)
    {
        this.searcherId = searcherId;
    }

    public Date getLastSearchTime()
    {
        return lastSearchTime;
    }

    public void setLastSearchTime(Date lastSearchTime)
    {
        this.lastSearchTime = lastSearchTime;
    }

    public ItemType getItemType()
    {
        return itemType;
    }

    public void setItemType(ItemType itemType)
    {
        this.itemType = itemType;
    }

    public List<SearchedItem> getStoredItems()
    {
        return storedItems;
    }

    public void setStoredItems(List<SearchedItem> storedItems)
    {
        this.storedItems = storedItems;
    }
}
