package com.patimer.notifier.service.engine;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dao.SearcherDao;
import com.patimer.notifier.model.*;
import com.patimer.notifier.service.WorkerTest;
import com.patimer.notifier.service.engine.worker.SearcherWorkerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestSearcherScheduler
{
    @Mock
    private AccountDao accountDaoMock;

    @Mock
    private SearcherDao searcherDaoMock;

    @Mock
    private SearcherWorkerFactory searcherWorkerFactoryMock;

    private SearcherScheduler searcherScheduler;

    @Before
    public void setUp()
    {
        this.searcherScheduler = new SearcherScheduler(accountDaoMock, searcherDaoMock, searcherWorkerFactoryMock);
        this.searcherScheduler.setThreadPoolCoreSize(1);
        this.searcherScheduler.setThreadPoolMaxSize(2);
        this.searcherScheduler.setThreadPoolQueueCapacity(1);
        this.searcherScheduler.setEngineSchedulerCronExpression("0 0 12 1/1 * ?");
        this.searcherScheduler.init();
    }

    @Test
    public void testPerformWhenNoAccountEntities()
    {
        // given
        List<AccountEntity> accountEntityInPage1 = Collections.emptyList();
        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);

        // when
        searcherScheduler.perform();

        // then
        verifyZeroInteractions(searcherWorkerFactoryMock); // no worker has been created.
        verifyZeroInteractions(searcherDaoMock); // no access to searcher database has been made.
    }

    @Test
    public void testPerformWhenOnePageOfAccountEntities()
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        List<AccountEntity> accountEntityInPage1 = Collections.singletonList(accountEntity);
        List<AccountEntity> accountEntityInPage2 = Collections.emptyList();
        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);
        when(accountDaoMock.findAll(1)).thenReturn(accountEntityInPage2);

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).build();
        List<SearcherEntity> searcherEntities = Collections.singletonList(searcherEntity);
        when(searcherDaoMock.findByAccountId(accountEntity.getId())).thenReturn(searcherEntities);

        WorkerTest workerTest = new WorkerTest();
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity, searcherEntities)).thenReturn(workerTest);

        // when
        searcherScheduler.perform();

        // then
        Assert.isTrue(workerTest.isDone());

        verify(searcherWorkerFactoryMock, times(1)).createSearcherWorker(accountEntity, searcherEntities);
    }

    @Test
    public void testPerformWhenMultiplePagesOfAccountEntities()
    {
        // given
        AccountEntity accountEntity1 = new AccountEntityBuilder().build();
        AccountEntity accountEntity2 = new AccountEntityBuilder().build();
        AccountEntity accountEntity3 = new AccountEntityBuilder().build();
        AccountEntity accountEntity4 = new AccountEntityBuilder().build();


        List<AccountEntity> accountEntityInPage1 = Arrays.asList(accountEntity1, accountEntity2);
        List<AccountEntity> accountEntityInPage2 = Arrays.asList(accountEntity3, accountEntity4);
        List<AccountEntity> accountEntityInPage3 = Collections.emptyList();

        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);
        when(accountDaoMock.findAll(2)).thenReturn(accountEntityInPage2);
        when(accountDaoMock.findAll(3)).thenReturn(accountEntityInPage3);

        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withAccountId(accountEntity1.getId()).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withAccountId(accountEntity2.getId()).build();
        SearcherEntity searcherEntity3 = new SearcherEntityBuilder().withAccountId(accountEntity3.getId()).build();
        SearcherEntity searcherEntity4 = new SearcherEntityBuilder().withAccountId(accountEntity3.getId()).build();


        List<SearcherEntity> searcherEntitiesForAccount1 = Collections.singletonList(searcherEntity1);
        List<SearcherEntity> searcherEntitiesForAccount2 = Collections.singletonList(searcherEntity2);
        List<SearcherEntity> searcherEntitiesForAccount3 = Collections.singletonList(searcherEntity3);
        List<SearcherEntity> searcherEntitiesForAccount4 = Collections.singletonList(searcherEntity4);

        when(searcherDaoMock.findByAccountId(accountEntity1.getId())).thenReturn(searcherEntitiesForAccount1);
        when(searcherDaoMock.findByAccountId(accountEntity2.getId())).thenReturn(searcherEntitiesForAccount2);
        when(searcherDaoMock.findByAccountId(accountEntity3.getId())).thenReturn(searcherEntitiesForAccount3);
        when(searcherDaoMock.findByAccountId(accountEntity4.getId())).thenReturn(searcherEntitiesForAccount4);


        WorkerTest workerTest1 = new WorkerTest();
        WorkerTest workerTest2 = new WorkerTest();
        WorkerTest workerTest3 = new WorkerTest();
        WorkerTest workerTest4 = new WorkerTest();
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity1, searcherEntitiesForAccount1)).thenReturn(workerTest1);
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity2, searcherEntitiesForAccount2)).thenReturn(workerTest2);
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity3, searcherEntitiesForAccount3)).thenReturn(workerTest3);
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity4, searcherEntitiesForAccount4)).thenReturn(workerTest4);


        // when
        searcherScheduler.perform();

        // then
        Assert.isTrue(workerTest1.isDone());
        Assert.isTrue(workerTest2.isDone());
        Assert.isTrue(workerTest3.isDone());
        Assert.isTrue(workerTest4.isDone());

        verify(searcherWorkerFactoryMock, times(4)).createSearcherWorker(any(AccountEntity.class), any(List.class));
    }

    @Test
    public void testPerformWhenDisabledAccountExists()
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().withAccountState(AccountState.Disabled).build();
        List<AccountEntity> accountEntityInPage1 = Collections.singletonList(accountEntity);
        List<AccountEntity> accountEntityInPage2 = Collections.emptyList();
        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);
        when(accountDaoMock.findAll(1)).thenReturn(accountEntityInPage2);

        // when
        searcherScheduler.perform();

        // then
        verifyZeroInteractions(searcherWorkerFactoryMock); // no worker has been created.
        verifyZeroInteractions(searcherDaoMock); // no access to searcher database has been made.
    }

    @Test
    public void testPerformWhenNoSearcherEntitiesForAccount()
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        List<AccountEntity> accountEntityInPage1 = Collections.singletonList(accountEntity);
        List<AccountEntity> accountEntityInPage2 = Collections.emptyList();
        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);
        when(accountDaoMock.findAll(1)).thenReturn(accountEntityInPage2);

        List<SearcherEntity> searcherEntities = Collections.emptyList();
        when(searcherDaoMock.findByAccountId(accountEntity.getId())).thenReturn(searcherEntities);

        // when
        searcherScheduler.perform();

        // then
        verifyZeroInteractions(searcherWorkerFactoryMock);
    }

    @Test
    public void testWhenSearcherWorkerFailed()
    {
        // given
        AccountEntity accountEntity1 = new AccountEntityBuilder().build();
        AccountEntity accountEntity2 = new AccountEntityBuilder().build();
        AccountEntity accountEntity3 = new AccountEntityBuilder().build();

        List<AccountEntity> accountEntityInPage1 = Arrays.asList(accountEntity1, accountEntity2);
        List<AccountEntity> accountEntityInPage2 = Collections.singletonList(accountEntity3);
        List<AccountEntity> accountEntityInPage3 = Collections.emptyList();

        when(accountDaoMock.findAll(0)).thenReturn(accountEntityInPage1);
        when(accountDaoMock.findAll(2)).thenReturn(accountEntityInPage2);
        when(accountDaoMock.findAll(3)).thenReturn(accountEntityInPage3);

        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withAccountId(accountEntity1.getId()).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withAccountId(accountEntity2.getId()).build();
        SearcherEntity searcherEntity3 = new SearcherEntityBuilder().withAccountId(accountEntity3.getId()).build();

        List<SearcherEntity> searcherEntitiesForAccount1 = Collections.singletonList(searcherEntity1);
        List<SearcherEntity> searcherEntitiesForAccount2 = Collections.singletonList(searcherEntity2);
        List<SearcherEntity> searcherEntitiesForAccount3 = Collections.singletonList(searcherEntity3);

        when(searcherDaoMock.findByAccountId(accountEntity1.getId())).thenReturn(searcherEntitiesForAccount1);
        when(searcherDaoMock.findByAccountId(accountEntity2.getId())).thenReturn(searcherEntitiesForAccount2);
        when(searcherDaoMock.findByAccountId(accountEntity3.getId())).thenReturn(searcherEntitiesForAccount3);


        WorkerTest workerTest1 = new WorkerTest();
        WorkerTest workerTest2 = new WorkerTest(true /*simulateException*/);
        WorkerTest workerTest3 = new WorkerTest();
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity1, searcherEntitiesForAccount1)).thenReturn(workerTest1);
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity2, searcherEntitiesForAccount2)).thenReturn(workerTest2);
        when(searcherWorkerFactoryMock.createSearcherWorker(accountEntity3, searcherEntitiesForAccount3)).thenReturn(workerTest3);

        // when
        searcherScheduler.perform();

        // then
        Assert.isTrue(workerTest1.isDone());
        Assert.isTrue(!workerTest2.isDone());
        Assert.isTrue(workerTest3.isDone());

        verify(searcherWorkerFactoryMock, times(3)).createSearcherWorker(any(AccountEntity.class), any(List.class));
    }
}
