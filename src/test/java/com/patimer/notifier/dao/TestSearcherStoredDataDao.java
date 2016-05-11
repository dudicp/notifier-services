package com.patimer.notifier.dao;

import com.patimer.notifier.model.SearcherStoredDataEntity;
import com.patimer.notifier.model.SearcherStoredDataEntityBuilder;
import com.patimer.notifier.service.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TestSearcherStoredDataDao extends AbstractDaoTest
{
    @Autowired
    private SearcherStoredDataDao dao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Upsert
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpsertWhenNotFound()
    {
        // given
        SearcherStoredDataEntity entity = new SearcherStoredDataEntityBuilder().build();

        // when
        SearcherStoredDataEntity storedEntity = dao.upsert(entity);

        // then
        Assert.notNull(storedEntity);
        SearcherStoredDataEntity foundEntity = dao.getById(storedEntity.getSearcherId());
        Assert.notNull(foundEntity);
    }

    @Test
    public void testUpsertWhenFound()
    {
        // given
        SearcherStoredDataEntity entity = new SearcherStoredDataEntityBuilder().build();
        SearcherStoredDataEntity createdEntity = dao.upsert(entity);
        Date currentTime = Calendar.getInstance().getTime();
        createdEntity.setLastSearchTime(currentTime);

        // when
        SearcherStoredDataEntity updatedEntity = dao.upsert(createdEntity);

        // then
        Assert.notNull(updatedEntity);
        SearcherStoredDataEntity foundEntity = dao.getById(updatedEntity.getSearcherId());
        Assert.notNull(foundEntity);
        Assert.isTrue(foundEntity.getLastSearchTime().equals(currentTime));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullEntity()
    {
        // given

        // when
        dao.upsert(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDeleteWhenFound()
    {
        // given
        SearcherStoredDataEntity entity = new SearcherStoredDataEntityBuilder().build();
        SearcherStoredDataEntity createdEntity = dao.upsert(entity);

        // when
        dao.delete(createdEntity.getSearcherId());

        // then
        SearcherStoredDataEntity foundEntity = dao.findById(createdEntity.getSearcherId());
        Assert.isNull(foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        dao.delete(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullId()
    {
        // given

        // when
        dao.delete(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetByIdWhenFound()
    {
        // given
        SearcherStoredDataEntity entity = new SearcherStoredDataEntityBuilder().build();
        SearcherStoredDataEntity createdEntity = dao.upsert(entity);

        // when
        SearcherStoredDataEntity foundEntity = dao.getById(createdEntity.getSearcherId());

        // then
        Assert.notNull(foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void testGetByIdWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        dao.getById(id);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNullId()
    {
        // given

        // when
        dao.getById(null);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  FindById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testfindByIdWhenFound()
    {
        // given
        SearcherStoredDataEntity entity = new SearcherStoredDataEntityBuilder().build();
        SearcherStoredDataEntity createdEntity = dao.upsert(entity);

        // when
        SearcherStoredDataEntity foundEntity = dao.findById(createdEntity.getSearcherId());

        // then
        Assert.notNull(foundEntity);
    }

    @Test
    public void testFindByIdWhenNotFound()
    {
        // given
        UUID id = UUID.randomUUID();

        // when
        SearcherStoredDataEntity foundEntity = dao.findById(id);

        // then
        Assert.isNull(foundEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdWithNullId()
    {
        // given

        // when
        dao.findById(null);

        // then - expected exception
    }
}
