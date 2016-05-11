package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.service.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface SearcherDao
{
    SearcherEntity create(SearcherEntity searcherEntity);

    SearcherEntity update(SearcherEntity mergedSearcherEntity);

    void delete(UUID id) throws NotFoundException;

    SearcherEntity getById(UUID id) throws NotFoundException;

    List<SearcherEntity> findByAccountId(UUID accountId);
}
