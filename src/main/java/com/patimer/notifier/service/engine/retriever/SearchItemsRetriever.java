package com.patimer.notifier.service.engine.retriever;

import com.patimer.notifier.model.SourceWebsiteType;
import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.item.SearchedItem;

import java.io.IOException;
import java.util.List;

public interface SearchItemsRetriever<T extends SearchedItem>
{
    List<T> retrieve() throws IOException;

    SourceWebsiteType getWebsiteType();

    ItemType getItemType();
}
