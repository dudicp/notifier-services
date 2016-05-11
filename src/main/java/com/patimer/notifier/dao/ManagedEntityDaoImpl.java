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
import java.util.UUID;

public abstract class ManagedEntityDaoImpl<T extends ManagedEntity>
{
    protected MongoTemplate mongoTemplate;
    protected EntityValidator validator;
    protected String collectionName;
    protected Class<T> clazz;

    public ManagedEntityDaoImpl(
        MongoTemplate mongoTemplate,
        EntityValidator validator,
        String collectionName,
        Class<T> clazz)
    {
        Validate.notNull(mongoTemplate);
        Validate.notNull(validator);
        Validate.notEmpty(collectionName);
        Validate.notNull(clazz);

        this.mongoTemplate = mongoTemplate;
        this.validator = validator;
        this.collectionName = collectionName;
        this.clazz = clazz;
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
        mongoTemplate.save(entity, collectionName);

        return entity;
    }

    public T update(T mergedEntity)
    {
        Validate.notNull(mergedEntity);

        Date currentDate = getCurrentDate();
        mergedEntity.setModifiedOn(currentDate);
        validator.validate(mergedEntity);

        validateExists(mergedEntity.getId());
        mongoTemplate.save(mergedEntity, collectionName);

        return mergedEntity;
    }

    public void delete(UUID id) throws NotFoundException
    {
        Validate.notNull(id);

        validateExists(id);
        Query byId = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(byId, clazz, collectionName);
    }

    public T getById(UUID id) throws NotFoundException
    {
        Validate.notNull(id);

        T existingEntity =
            mongoTemplate.findById(id, clazz, collectionName);

        if(existingEntity == null)
            throw new NotFoundException("entity not found for id: '" + id + "'.");

        return existingEntity;
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
