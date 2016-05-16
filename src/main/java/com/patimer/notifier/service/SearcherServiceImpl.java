package com.patimer.notifier.service;

import com.patimer.notifier.dao.AccountDao;
import com.patimer.notifier.dao.SearcherDao;
import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountState;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.converter.SearcherConverter;
import com.patimer.notifier.service.engine.worker.SearcherWorkerFactory;
import com.patimer.notifier.service.exception.NotFoundException;
import com.patimer.notifier.service.validation.DtoValidator;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service(value = "searcherService")
public class SearcherServiceImpl implements SearcherService
{
    private final static Logger log = LogManager.getLogger(SearcherServiceImpl.class);

    @Value("${api.search.thread.pool.core.size:2}")
    private int threadPoolCoreSize;

    @Value("${api.search.thread.pool.max.size:4}")
    private int threadPoolMaxSize;

    @Value("${api.search.thread.pool.queue.capacity:30}")
    private int threadPoolQueueCapacity;

    private AccountDao accountDao;
    private SearcherDao searcherDao;
    private SearcherConverter searcherConverter;
    private DtoValidator dtoValidator;
    private SearcherWorkerFactory searcherWorkerFactory;
    private ThreadPoolTaskExecutor taskExecutor;


    @Autowired
    public SearcherServiceImpl(AccountDao accountDao, SearcherDao searcherDao, SearcherConverter searcherConverter, DtoValidator dtoValidator, SearcherWorkerFactory searcherWorkerFactory)
    {
        Validate.notNull(accountDao);
        Validate.notNull(searcherDao);
        Validate.notNull(searcherConverter);
        Validate.notNull(dtoValidator);
        Validate.notNull(searcherWorkerFactory);

        this.accountDao = accountDao;
        this.searcherDao = searcherDao;
        this.searcherConverter = searcherConverter;
        this.dtoValidator = dtoValidator;
        this.searcherWorkerFactory = searcherWorkerFactory;
    }

    @PostConstruct
    public void init()
    {
        Validate.isTrue(threadPoolCoreSize > 0);
        Validate.isTrue(threadPoolMaxSize > 0 && threadPoolMaxSize >= threadPoolCoreSize);
        Validate.isTrue(threadPoolQueueCapacity > 0);

        this.taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadPoolCoreSize);
        taskExecutor.setMaxPoolSize(threadPoolMaxSize);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setQueueCapacity(threadPoolQueueCapacity);
        taskExecutor.initialize();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CRUD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public SearcherDto create(SessionPrincipal sessionPrincipal, SearcherDto searcherDto)
    {
        Validate.notNull(sessionPrincipal);
        Validate.notNull(searcherDto);
        validateAccountIdInDtoMatchSessionPrincipalOwner(sessionPrincipal, searcherDto);
        dtoValidator.validateManagedObjectForCreate(searcherDto);

        SearcherEntity searcherEntity = searcherConverter.convertForCreate(searcherDto);
        SearcherEntity storedSearcherEntity = searcherDao.create(searcherEntity);

        return searcherConverter.convertToDto(storedSearcherEntity);
    }

    @Override
    public SearcherDto update(SessionPrincipal sessionPrincipal, SearcherDto searcherDto) throws NotFoundException
    {
        Validate.notNull(sessionPrincipal);
        Validate.notNull(searcherDto);
        validateAccountIdInDtoMatchSessionPrincipalOwner(sessionPrincipal, searcherDto);
        dtoValidator.validateManagedObjectForCreate(searcherDto);

        SearcherEntity existingSearcherEntity = getByIdAndValidateAuthorization(sessionPrincipal, searcherDto.getId());
        SearcherEntity mergedSearcherEntity = searcherConverter.mergeForUpdate(searcherDto, existingSearcherEntity);
        SearcherEntity storedSearcherEntity = searcherDao.update(mergedSearcherEntity);

        return searcherConverter.convertToDto(storedSearcherEntity);
    }

    @Override
    public void delete(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException
    {
        Validate.notNull(sessionPrincipal);
        Validate.notNull(searcherId);

        getByIdAndValidateAuthorization(sessionPrincipal, searcherId);
        searcherDao.delete(searcherId);
    }

    @Override
    public SearcherDto getById(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException
    {
        Validate.notNull(sessionPrincipal);
        Validate.notNull(searcherId);

        SearcherEntity existingSearcherEntity = getByIdAndValidateAuthorization(sessionPrincipal, searcherId);
        return searcherConverter.convertToDto(existingSearcherEntity);
    }

    @Override
    public List<SearcherDto> findByCurrentAccountId(SessionPrincipal sessionPrincipal)
    {
        Validate.notNull(sessionPrincipal);

        List<SearcherDto> results = new ArrayList<>();
        List<SearcherEntity> searcherEntities = searcherDao.findByAccountId(sessionPrincipal.getAccountId());

        if(searcherEntities != null)
        {
            for(SearcherEntity searcherEntity : searcherEntities)
            {
                SearcherDto searcherDto = searcherConverter.convertToDto(searcherEntity);
                results.add(searcherDto);
            }
        }

        return results;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Operation(s)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void perform(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException
    {
        Validate.notNull(sessionPrincipal);
        Validate.notNull(searcherId);

        SearcherEntity existingSearcherEntity = getByIdAndValidateAuthorization(sessionPrincipal, searcherId);
        AccountEntity accountEntity = accountDao.getById(sessionPrincipal.getAccountId());

        if(accountEntity.getAccountState() != AccountState.Activated) {
            throw new IllegalStateException(
                "Invalid account state to perform search: '" + accountEntity.getAccountState() + "'."
            );
        }

        log.info("Starting searcher for '" + sessionPrincipal.getEmail() + "' ('" + searcherId + "').");

        Runnable searcherWorker =
            searcherWorkerFactory.createSearcherWorker(accountEntity, Collections.singletonList(existingSearcherEntity));

        taskExecutor.submit(searcherWorker);

        log.info("Searcher completed successfully for '" + sessionPrincipal.getEmail() + "' ('" + searcherId + "').");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Setters (for unit-tests)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void validateAccountIdInDtoMatchSessionPrincipalOwner(SessionPrincipal sessionPrincipal, SearcherDto searcherDto)
    {
        Validate.isTrue(sessionPrincipal.getAccountId().equals(searcherDto.getAccountId()));
    }

    private SearcherEntity getByIdAndValidateAuthorization(SessionPrincipal sessionPrincipal, UUID searcherId)
    {
        SearcherEntity existingSearcherEntity = searcherDao.getById(searcherId);
        Validate.isTrue(existingSearcherEntity.getAccountId().equals(sessionPrincipal.getAccountId()));
        return existingSearcherEntity;
    }
}
