package com.patimer.notifier.dao;

import com.patimer.notifier.model.ManagedEntity;
import com.patimer.notifier.service.validation.EntityValidator;
import com.patimer.notifier.service.exception.NotFoundException;
import org.apache.commons.lang.Validate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class ManagedEntityDaoImpl<T extends ManagedEntity>
{
    private final static int DEFAULT_PAGE_SIZE = 1000;

    protected MongoTemplate mongoTemplate;
    protected EntityValidator validator;
    protected DbCollectionType dbCollectionType;
    protected Class<T> clazz;
    protected int pageSize;

    public ManagedEntityDaoImpl(
        MongoTemplate mongoTemplate,
        EntityValidator validator,
        DbCollectionType dbCollectionType,
        Class<T> clazz)
    {
        Validate.notNull(mongoTemplate);
        Validate.notNull(validator);
        Validate.notNull(dbCollectionType);
        Validate.notNull(clazz);

        this.mongoTemplate = mongoTemplate;
        this.validator = validator;
        this.dbCollectionType = dbCollectionType;
        this.clazz = clazz;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    protected void setPageSize(int pageSize)
    {
        Validate.isTrue(pageSize > 0);
        this.pageSize = pageSize;
    }

    public T create(T entity)
    {
        Validate.notNull(entity);

        UUID generatedId = UUID.randomUUID();
        Date currentDate = getCurrentDate();
        entity.setId(generatedId);
        entity.setCreatedOn(currentDate);
        entity.setModifiedOn(currentDate);

        validator.validate(entity);
        mongoTemplate.save(entity, dbCollectionType.getDbTableName());

        return entity;
    }

    public T update(T mergedEntity)
    {
        Validate.notNull(mergedEntity);

        Date currentDate = getCurrentDate();
        mergedEntity.setModifiedOn(currentDate);
        validator.validate(mergedEntity);

        validateExists(mergedEntity.getId());
        mongoTemplate.save(mergedEntity, dbCollectionType.getDbTableName());

        return mergedEntity;
    }

    public void delete(UUID id) throws NotFoundException
    {
        Validate.notNull(id);

        validateExists(id);
        Query byId = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(byId, clazz, dbCollectionType.getDbTableName());
    }

    public T getById(UUID id) throws NotFoundException
    {
        Validate.notNull(id);

        T existingEntity =
            mongoTemplate.findById(id, clazz, dbCollectionType.getDbTableName());

        if(existingEntity == null)
            throw new NotFoundException("entity not found for id: '" + id + "'.");

        return existingEntity;
    }

    public List<T> findAll(int offset)
    {
        Validate.isTrue(offset >= 0);
        Query pageQuery = new Query().limit(pageSize).skip(offset);
        return mongoTemplate.find(pageQuery, clazz, dbCollectionType.getDbTableName());
    }

    private void validateExists(UUID id)
    {
        getById(id);
    }

    private Date getCurrentDate()
    {
        return Calendar.getInstance().getTime();
    }
}
