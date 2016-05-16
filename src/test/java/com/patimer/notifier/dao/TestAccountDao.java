package com.patimer.notifier.dao;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.service.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
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
        accountDao.create(null);

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
    public void testFindByMailWhenNotFound()
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  FindAll
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindAllWhenFound()
    {
        // given
        int offset = 0;
        AccountEntity accountEntity =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity storedAccountEntity = accountDao.create(accountEntity);

        // when
        List<AccountEntity> foundAccountEntities = accountDao.findAll(offset);

        // then
        Assert.notNull(foundAccountEntities);
        Assert.isTrue(foundAccountEntities.size() == 1);
    }

    @Test
    public void testFindAllWhenNotFound()
    {
        // given
        int offset = 0;

        // when
        List<AccountEntity> foundAccountEntities = accountDao.findAll(offset);

        // then
        Assert.isTrue(foundAccountEntities == null || foundAccountEntities.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllWithInvalidOffset()
    {
        // given
        int offset = -1;

        // when
        accountDao.findAll(offset);

        // then - expected exception
    }

    @Test
    public void testFindAllPagination()
    {
        // given
        int offset = 0;
        ((ManagedEntityDaoImpl)accountDao).setPageSize(2);

        AccountEntity accountEntity1 =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        AccountEntity accountEntity2 =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test2").build();
        AccountEntity accountEntity3 =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test3").build();
        AccountEntity accountEntity4 =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test4").build();
        AccountEntity accountEntity5 =
            new AccountEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test5").build();
        accountDao.create(accountEntity1);
        accountDao.create(accountEntity2);
        accountDao.create(accountEntity3);
        accountDao.create(accountEntity4);
        accountDao.create(accountEntity5);

        // when
        List<AccountEntity> foundAccountEntitiesPage1 = accountDao.findAll(offset);
        List<AccountEntity> foundAccountEntitiesPage2 = accountDao.findAll(foundAccountEntitiesPage1.size());
        List<AccountEntity> foundAccountEntitiesPage3 = accountDao.findAll(foundAccountEntitiesPage1.size() + foundAccountEntitiesPage2.size());


        // then
        Assert.notNull(foundAccountEntitiesPage1);
        Assert.notNull(foundAccountEntitiesPage2);
        Assert.notNull(foundAccountEntitiesPage3);
        Assert.isTrue(foundAccountEntitiesPage1.size() == 2);
        Assert.isTrue(foundAccountEntitiesPage2.size() == 2);
        Assert.isTrue(foundAccountEntitiesPage3.size() == 1);
    }
}
