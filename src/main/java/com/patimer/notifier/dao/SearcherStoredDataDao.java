package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherStoredDataEntity;

import java.util.UUID;

public interface SearcherStoredDataDao
{
    SearcherStoredDataEntity upsert(SearcherStoredDataEntity searcherStoredDataEntity);

    void delete(UUID id);

    SearcherStoredDataEntity getById(UUID id);

    SearcherStoredDataEntity findById(UUID id);

}
