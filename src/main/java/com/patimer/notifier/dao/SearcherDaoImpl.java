package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.service.validation.EntityValidator;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SearcherDaoImpl extends ManagedEntityDaoImpl<SearcherEntity> implements SearcherDao
{
    @Autowired
    public SearcherDaoImpl(MongoTemplate mongoTemplate, EntityValidator validator)
    {
        super(mongoTemplate, validator, DbCollectionType.Searchers, SearcherEntity.class);
    }

    @Override
    public List<SearcherEntity> findByAccountId(UUID accountId)
    {
        Validate.notNull(accountId);

        Query byAccountId = new Query(Criteria.where("accountId").is(accountId));
        return mongoTemplate.find(byAccountId, SearcherEntity.class, DbCollectionType.Searchers.getDbTableName());
    }
}
