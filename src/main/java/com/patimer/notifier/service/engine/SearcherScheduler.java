package com.patimer.notifier.service.engine;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dao.SearcherDao;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.service.engine.worker.SearcherWorkerFactory;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class SearcherScheduler
{
    private static final Logger log = LogManager.getLogger(SearcherScheduler.class);

    @Value("${engine.scheduler.cron.expression}")
    private String engineSchedulerCronExpression;

    @Value("${engine.thread.pool.core.size:20}")
    private int threadPoolCoreSize;

    @Value("${engine.thread.pool.max.size:30}")
    private int threadPoolMaxSize;

    @Value("${engine.thread.pool.queue.capacity:1000}")
    private int threadPoolQueueCapacity;

    private AccountDao accountDao;
    private SearcherDao searcherDao;
    private SearcherWorkerFactory searcherWorkerFactory;
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public SearcherScheduler(AccountDao accountDao, SearcherDao searcherDao, SearcherWorkerFactory searcherWorkerFactory)
    {
        Validate.notNull(accountDao);
        Validate.notNull(searcherDao);
        Validate.notNull(searcherWorkerFactory);

        this.accountDao = accountDao;
        this.searcherDao = searcherDao;
        this.searcherWorkerFactory = searcherWorkerFactory;
    }

    @PostConstruct
    public void init()
    {
        Validate.isTrue(threadPoolCoreSize > 0);
        Validate.isTrue(threadPoolMaxSize > 0 && threadPoolMaxSize >= threadPoolCoreSize);
        Validate.isTrue(threadPoolQueueCapacity > 0);
        Validate.notEmpty(engineSchedulerCronExpression);

        this.taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadPoolCoreSize);
        taskExecutor.setMaxPoolSize(threadPoolMaxSize);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setQueueCapacity(threadPoolQueueCapacity);
        taskExecutor.initialize();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Thread Pool Configurations (for unit-tests)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void setThreadPoolCoreSize(int threadPoolCoreSize)
    {
        this.threadPoolCoreSize = threadPoolCoreSize;
    }

    protected void setThreadPoolMaxSize(int threadPoolMaxSize)
    {
        this.threadPoolMaxSize = threadPoolMaxSize;
    }

    protected void setThreadPoolQueueCapacity(int threadPoolQueueCapacity)
    {
        this.threadPoolQueueCapacity = threadPoolQueueCapacity;
    }

    protected void setEngineSchedulerCronExpression(String engineSchedulerCronExpression)
    {
        this.engineSchedulerCronExpression = engineSchedulerCronExpression;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Thread Pool Configurations (for unit-tests)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Scheduled(cron = "${engine.scheduler.cron.expression}")
    public void perform()
    {
        log.info("Starting engine searcher scheduler.");

        // Note: the iteration is done on accounts and not searchers because the notification should be aggregated
        // for items found on all searchers for specific account.
        int accountCounter = 0;
        int searcherCounter = 0;

        int offset = 0;
        List<AccountEntity> accountEntities = accountDao.findAll(offset);

        // iteration is done on pages
        while(accountEntities != null && !accountEntities.isEmpty())
        {
            for(AccountEntity accountEntity : accountEntities)
            {
                if(accountEntity.getAccountState() == AccountState.Activated)
                {
                    List<SearcherEntity> searcherEntities = searcherDao.findByAccountId(accountEntity.getId());

                    if(searcherEntities != null && !searcherEntities.isEmpty())
                    {
                        Runnable runnableSearcherWorker =
                            searcherWorkerFactory.createSearcherWorker(accountEntity, searcherEntities);

                        taskExecutor.submit(runnableSearcherWorker);

                        accountCounter++;
                        searcherCounter = searcherCounter + searcherEntities.size();
                    }
                }
            }

            // wait for this page threads to complete and then reinitialize the task executor to accept tasks.
            awaitTerminationForTaskExecutor();
            reinitializeTaskExecutor();

            // continue to next page
            offset = offset + accountEntities.size();
            accountEntities = accountDao.findAll(offset);
        }

        log.info(
            "Engine searcher scheduler completed successfully (total accounts: " +
            accountCounter +
            ", total searchers: " +
            searcherCounter + ")."
        );
    }

    private void awaitTerminationForTaskExecutor()
    {
        taskExecutor.shutdown();

        try
        {
            taskExecutor.getThreadPoolExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e)
        {
            log.error("Failed to await for task executer termination.", e);
        }
    }

    private void reinitializeTaskExecutor()
    {
        taskExecutor.initialize();
    }
}
