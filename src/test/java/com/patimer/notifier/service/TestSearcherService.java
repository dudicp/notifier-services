package com.patimer.notifier.service;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dao.SearcherDao;
import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.dto.SearcherDtoBuilder;
import com.patimer.notifier.model.*;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.converter.SearcherConverter;
import com.patimer.notifier.service.engine.worker.SearcherWorkerFactory;
import com.patimer.notifier.service.validation.DtoValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestSearcherService
{
    private SearcherServiceImpl searcherService;

    @Mock
    private AccountDao accountDaoMock;

    @Mock
    private SearcherDao searcherDaoMock;

    @Mock
    private SearcherWorkerFactory searcherWorkerFactoryMock;

    @Before
    public void setUp()
    {
        SearcherConverter searcherConverter = new SearcherConverter();
        DtoValidator dtoValidator = new DtoValidator();

        searcherService =
            new SearcherServiceImpl(
                accountDaoMock,
                searcherDaoMock,
                searcherConverter,
                dtoValidator,
                searcherWorkerFactoryMock
            );

        searcherService.setThreadPoolCoreSize(1);
        searcherService.setThreadPoolMaxSize(2);
        searcherService.setThreadPoolQueueCapacity(1);
        searcherService.init();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        SearcherEntity createdSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.create(any(SearcherEntity.class))).thenReturn(createdSearcherEntity);

        // when
        SearcherDto createdSearcherDto = searcherService.create(sessionPrincipal, searcherDto);

        // then
        Assert.notNull(createdSearcherDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullSession()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();

        // when
        searcherService.create(null, searcherDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullSearcherDto()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.create(sessionPrincipal, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWhenAccountIdInDtoAndSessionIsDifferent()
    {
        // given
        SearcherDto searcherDto = new SearcherDtoBuilder().build();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.create(sessionPrincipal, searcherDto);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdate()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.getById(searcherDto.getId())).thenReturn(existingSearcherEntity);
        SearcherEntity updatedSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.update(any(SearcherEntity.class))).thenReturn(updatedSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        SearcherDto updatedSearcherDto = searcherService.update(sessionPrincipal, searcherDto);

        // then
        Assert.notNull(updatedSearcherDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNullSession()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();

        // when
        searcherService.update(null, searcherDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNullSearcherDto()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.update(sessionPrincipal, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWhenAccountIdInDtoAndSessionIsDifferent()
    {
        // given
        SearcherDto searcherDto = new SearcherDtoBuilder().build();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.update(sessionPrincipal, searcherDto);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWhenAccountIdInExistingSearcherEntityAndInSessionIsDifferent()
    {
        // given
        SearcherDto searcherDto = new SearcherDtoBuilder().build();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().build();
        when(searcherDaoMock.getById(searcherDto.getId())).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.update(sessionPrincipal, searcherDto);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDelete()
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.delete(sessionPrincipal, searcherId);

        // then - no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullSession()
    {
        // given
        UUID searcherId = UUID.randomUUID();

        // when
        searcherService.delete(null, searcherId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullSearcherId()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.delete(sessionPrincipal, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWhenAccountIdInExistingSearcherEntityAndInSessionIsDifferent()
    {
        // given
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.delete(sessionPrincipal, searcherId);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetById()
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        SearcherDto searcherDto = searcherService.getById(sessionPrincipal, searcherId);

        // then
        Assert.notNull(searcherDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNullSession()
    {
        // given
        UUID searcherId = UUID.randomUUID();

        // when
        searcherService.getById(null, searcherId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNullSearcherId()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.getById(sessionPrincipal, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWhenAccountIdInExistingSearcherEntityAndInSessionIsDifferent()
    {
        // given
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.getById(sessionPrincipal, searcherId);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FindByCurrentAccountId
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindByCurrentAccountId()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withAccountId(accountId).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withAccountId(accountId).build();
        List<SearcherEntity> searcherEntityList = Arrays.asList(searcherEntity1, searcherEntity2);
        when(searcherDaoMock.findByAccountId(accountId)).thenReturn(searcherEntityList);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        List<SearcherDto> results = searcherService.findByCurrentAccountId(sessionPrincipal);

        // then
        Assert.notNull(results);
        Assert.isTrue(results.size() == searcherEntityList.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByCurrentAccountIdWithNullSession()
    {
        // given

        // when
        searcherService.findByCurrentAccountId(null);

        // then - expected exception
    }

    @Test
    public void testFindByCurrentAccountIdWhenNoFound()
    {
        // given
        UUID accountId = UUID.randomUUID();
        List<SearcherEntity> searcherEntityList = Collections.emptyList();
        when(searcherDaoMock.findByAccountId(accountId)).thenReturn(searcherEntityList);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        List<SearcherDto> results = searcherService.findByCurrentAccountId(sessionPrincipal);

        // then
        Assert.isTrue(results == null || results.isEmpty());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Perform
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPerform()
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        AccountEntity accountEntity = new AccountEntityBuilder().withId(accountId).build();
        when(accountDaoMock.getById(accountId)).thenReturn(accountEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        WorkerTest workerTest = new WorkerTest();
        when(searcherWorkerFactoryMock.createSearcherWorker(any(AccountEntity.class), anyList())).thenReturn(workerTest);

        // when
        searcherService.perform(sessionPrincipal, searcherId);

        // then - no exception
    }

    @Test(expected = IllegalStateException.class)
    public void testPerformWhenAccountDisabled()
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().withAccountId(accountId).build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        AccountEntity accountEntity = new AccountEntityBuilder().withId(accountId).withAccountState(AccountState.Disabled).build();
        when(accountDaoMock.getById(accountId)).thenReturn(accountEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        WorkerTest workerTest = new WorkerTest();
        when(searcherWorkerFactoryMock.createSearcherWorker(any(AccountEntity.class), anyList())).thenReturn(workerTest);

        // when
        searcherService.perform(sessionPrincipal, searcherId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformWithNullSession()
    {
        // given
        UUID searcherId = UUID.randomUUID();

        // when
        searcherService.perform(null, searcherId);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformWithNullSearcherId()
    {
        // given
        UUID accountId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();

        // when
        searcherService.perform(sessionPrincipal, null);

        // then - expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformWhenAccountIdInExistingSearcherEntityAndInSessionIsDifferent()
    {
        // given
        UUID searcherId = UUID.randomUUID();
        SearcherEntity existingSearcherEntity = new SearcherEntityBuilder().build();
        when(searcherDaoMock.getById(searcherId)).thenReturn(existingSearcherEntity);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();

        // when
        searcherService.perform(sessionPrincipal, searcherId);

        // then - expected exception
    }
}
