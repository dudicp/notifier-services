package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.SearcherEntityBuilder;
import com.patimer.notifier.service.exception.NotFoundException;
import com.patimer.notifier.service.exception.ValidationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestSearcherDao extends AbstractDaoTest
{
    @Autowired
    private SearcherDao searcherDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate()
    {
        // given
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).build();

        // when
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // then
        Assert.notNull(storedSearcherEntity);
        Assert.notNull(storedSearcherEntity.getId());
        Assert.notNull(storedSearcherEntity.getCreatedOn());
        Assert.notNull(storedSearcherEntity.getModifiedOn());

        SearcherEntity foundSearcherEntity = searcherDao.getById(storedSearcherEntity.getId());
        Assert.notNull(foundSearcherEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullEntity()
    {
        // given

        // when
        SearcherEntity storedSearcherEntity = searcherDao.create(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateWhenFound()
    {
        // given
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // when
        Date modifiedOnUponCreate = storedSearcherEntity.getModifiedOn();
        storedSearcherEntity.setName("test2");
        SearcherEntity updatedSearcherEntity = searcherDao.update(storedSearcherEntity);

        // then
        Assert.notNull(updatedSearcherEntity);
        SearcherEntity foundSearcherEntity = searcherDao.getById(storedSearcherEntity.getId());
        Assert.notNull(foundSearcherEntity);
        Assert.isTrue(foundSearcherEntity.getName().equals("test2"));
        Assert.isTrue(foundSearcherEntity.getModifiedOn().after(modifiedOnUponCreate));
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateWhenNotFound()
    {
        // given
        SearcherEntity searcherEntity = new SearcherEntityBuilder().build();

        // when
        searcherDao.update(searcherEntity);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNullEntity()
    {
        // given

        // when
        searcherDao.update(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDeleteWhenFound()
    {
        // given
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // when
        searcherDao.delete(storedSearcherEntity.getId());

        // then
        try
        {
            searcherDao.getById(storedSearcherEntity.getId());
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
        searcherDao.delete(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullId()
    {
        // given

        // when
        searcherDao.delete(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetByIdWhenFound()
    {
        // given
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).withCreatedOn(null).withModifiedOn(null).withName("test1").build();
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // when
        SearcherEntity foundSearcherEntity = searcherDao.getById(storedSearcherEntity.getId());

        // then
        Assert.notNull(foundSearcherEntity);
    }

    @Test(expected = NotFoundException.class)
    public void testGetByIdWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        searcherDao.getById(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNullId()
    {
        // given

        // when
        searcherDao.getById(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  FindBYMail
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindByAccountIdWhenOneFound()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).withAccountId(accountId).build();
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // when
        List<SearcherEntity> foundSearcherEntities = searcherDao.findByAccountId(accountId);

        // then
        Assert.notNull(foundSearcherEntities);
        Assert.isTrue(foundSearcherEntities.size() == 1);
        Assert.isTrue(foundSearcherEntities.iterator().next().getId().equals(storedSearcherEntity.getId()));
    }

    @Test
    public void testFindByAccountIdWhenMultipleFound()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withName("test1").withAccountId(accountId).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withName("test2").withAccountId(accountId).build();
        SearcherEntity searcherEntity3 = new SearcherEntityBuilder().withName("test3").withAccountId(accountId).build();
        SearcherEntity searcherEntity4 = new SearcherEntityBuilder().withName("test4").build();
        SearcherEntity searcherEntity5 = new SearcherEntityBuilder().withName("test5").build();

        searcherDao.create(searcherEntity1);
        searcherDao.create(searcherEntity2);
        searcherDao.create(searcherEntity3);
        searcherDao.create(searcherEntity4);
        searcherDao.create(searcherEntity5);

        // when
        List<SearcherEntity> foundSearcherEntities = searcherDao.findByAccountId(accountId);

        // then
        Assert.notNull(foundSearcherEntities);
        Assert.isTrue(foundSearcherEntities.size() == 3);
    }

    @Test
    public void testFIndByAccountIdWhenNotFound()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherEntity searcherEntity =
            new SearcherEntityBuilder().withId(null).build();
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        // when
        List<SearcherEntity> foundSearcherEntities = searcherDao.findByAccountId(accountId);

        // then
        Assert.isTrue(foundSearcherEntities == null || foundSearcherEntities.size() == 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByAccountIdWithNullMail()
    {
        // given

        // when
        searcherDao.findByAccountId(null);

        // then - expected exception
    }
}
