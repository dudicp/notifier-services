package com.patimer.notifier.dao;

import com.patimer.notifier.service.exception.NotFoundException;
import com.patimer.notifier.service.validation.EntityValidator;
import org.apache.commons.lang.Validate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SimpleEntityDaoImpl<T,U>
{
    protected MongoTemplate mongoTemplate;
    protected EntityValidator validator;
    protected DbCollectionType dbCollectionType;
    protected Class<T> clazz;
    protected String idPropertyName;

    public SimpleEntityDaoImpl(
        MongoTemplate mongoTemplate,
        EntityValidator validator,
        DbCollectionType dbCollectionType,
        Class<T> clazz,
        String idPropertyName
    )
    {
        Validate.notNull(mongoTemplate);
        Validate.notNull(validator);
        Validate.notNull(dbCollectionType);
        Validate.notNull(clazz);
        Validate.notEmpty(idPropertyName);

        this.mongoTemplate = mongoTemplate;
        this.validator = validator;
        this.dbCollectionType = dbCollectionType;
        this.clazz = clazz;
        this.idPropertyName = idPropertyName;
    }

    public T upsert(T entity)
    {
        Validate.notNull(entity);
        validator.validate(entity);
        mongoTemplate.save(entity, dbCollectionType.getDbTableName());
        return entity;
    }

    public void delete(U id) throws NotFoundException
    {
        Validate.notNull(id);

        validateExists(id);
        Query byId = new Query(Criteria.where(idPropertyName).is(id));
        mongoTemplate.remove(byId, clazz, dbCollectionType.getDbTableName());
    }

    public T getById(U id) throws NotFoundException
    {
        Validate.notNull(id);

        T existingEntity =
            mongoTemplate.findById(id, clazz, dbCollectionType.getDbTableName());

        if(existingEntity == null)
            throw new NotFoundException("entity not found for id: '" + id + "'.");

        return existingEntity;
    }

    public T findById(U id)
    {
        Validate.notNull(id);

        return mongoTemplate.findById(id, clazz, dbCollectionType.getDbTableName());
    }

    private void validateExists(U id)
    {
        getById(id);
    }
}
