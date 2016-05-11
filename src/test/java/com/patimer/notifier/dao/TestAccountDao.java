package com.patimer.notifier.dao;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.service.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.UUID;

public class TestAccountDao extends AbstractDaoTest
{
    @Autowired
    private AccountDao accountDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate()
    {
        // given
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).build();

        // when
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // then
        Assert.notNull(storedAccountEntity);
        Assert.notNull(storedAccountEntity.getId());
        Assert.notNull(storedAccountEntity.getCreatedOn());
        Assert.notNull(storedAccountEntity.getModifiedOn());

        AccountEntity foundAccountEntity = accountDao.getById(storedAccountEntity.getId());
        Assert.notNull(foundAccountEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullEntity()
    {
        // given

        // when
        AccountEntity storedAccountEntity = accountDao.create(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateWhenFound()
    {
        // given
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // when
        Date modifiedOnUponCreate = storedAccountEntity.getModifiedOn();
        storedAccountEntity.setName("test2");
        AccountEntity updatedAccountEntity = accountDao.update(storedAccountEntity);

        // then
        Assert.notNull(updatedAccountEntity);
        AccountEntity foundAccountEntity = accountDao.getById(storedAccountEntity.getId());
        Assert.notNull(foundAccountEntity);
        Assert.isTrue(foundAccountEntity.getName().equals("test2"));
        Assert.isTrue(foundAccountEntity.getModifiedOn().after(modifiedOnUponCreate));
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateWhenNotFound()
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();

        // when
        accountDao.update(accountEntity);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNullEntity()
    {
        // given

        // when
        accountDao.update(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDeleteWhenFound()
    {
        // given
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // when
        accountDao.delete(storedAccountEntity.getId());

        // then
        try
        {
            accountDao.getById(storedAccountEntity.getId());
        }
        catch (NotFoundException e)
        {
            // ignore
            return;
        }

        throw new RuntimeException("test failed");
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        accountDao.delete(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullId()
    {
        // given

        // when
        accountDao.delete(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetByIdWhenFound()
    {
        // given
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // when
        AccountEntity foundAccountEntity = accountDao.getById(storedAccountEntity.getId());

        // then
        Assert.notNull(foundAccountEntity);
    }

    @Test(expected = NotFoundException.class)
    public void testGetByIdWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        accountDao.getById(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNullId()
    {
        // given

        // when
        accountDao.getById(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  FindBYMail
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindByMailWhenFound()
    {
        // given
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // when
        AccountEntity foundAccountEntity = accountDao.findByMail(storedAccountEntity.getMail());

        // then
        Assert.notNull(foundAccountEntity);
    }

    @Test
    public void testFIndByMailWhenNotFound()
    {
        // given
        String mail = "unknown@unknown.com";

        // when
        AccountEntity foundAccountEntity = accountDao.findByMail(mail);

        // then
        Assert.isNull(foundAccountEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByMailWithNullMail()
    {
        // given

        // when
        accountDao.findByMail(null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByMailWIthEmptyMail()
    {
        // given

        // when
        accountDao.findByMail("");

        // then - expected exception
    }
}
