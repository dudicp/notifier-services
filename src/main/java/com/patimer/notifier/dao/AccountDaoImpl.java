package com.patimer.notifier.dao;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.service.validation.EntityValidator;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;


@Component
public class AccountDaoImpl extends ManagedEntityDaoImpl<AccountEntity> implements AccountDao
{
    @Autowired
    public AccountDaoImpl(MongoTemplate mongoTemplate, EntityValidator validator)
    {
        super(mongoTemplate, validator, DbCollectionType.Accounts, AccountEntity.class);
    }

    @Override
    public AccountEntity findByMail(String mail)
    {
        Validate.notEmpty(mail);

        Query byMail = new Query(Criteria.where("mail").is(mail));
        return mongoTemplate.findOne(byMail, AccountEntity.class, DbCollectionType.Accounts.getDbTableName());
    }
}
