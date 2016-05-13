package com.patimer.notifier.service.engine;

import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.item.SearchedItem;

import java.io.IOException;
import java.util.List;

public interface SearcherService
{
    List<SearchedItem> searchNewOrUpdatedItems(SearcherEntity searcherEntity) throws IOException;
}
