package com.patimer.notifier.service.engine;

import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.item.SearchedItem;

import java.util.List;

public interface SearcherListener
{
    void notify(SearcherEntity searcherEntity, String email, List<SearchedItem> foundItems);
}
