package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherStoredDataEntity;
import com.patimer.notifier.service.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SearcherStoredDataDaoImpl extends SimpleEntityDaoImpl<SearcherStoredDataEntity, UUID> implements SearcherStoredDataDao
{
    @Autowired
    public SearcherStoredDataDaoImpl(MongoTemplate mongoTemplate, EntityValidator validator)
    {
        super(mongoTemplate, validator, DbCollectionType.SearcherStoredData, SearcherStoredDataEntity.class, "searcherId");
    }
}
