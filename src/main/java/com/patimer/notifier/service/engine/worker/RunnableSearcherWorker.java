package com.patimer.notifier.service.engine.worker;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.item.SearchedItem;
import com.patimer.notifier.service.engine.SearcherService;
import com.patimer.notifier.service.notification.NotificationService;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunnableSearcherWorker implements Runnable
{
    private static final Logger log = LogManager.getLogger(RunnableSearcherWorker.class);

    private AccountEntity accountEntity;
    private Map<UUID, SearcherEntity> searcherEntitiesMap;
    private SearcherService searcherService;
    private NotificationService notificationService;

    public RunnableSearcherWorker(AccountEntity accountEntity, List<SearcherEntity> searcherEntities, SearcherService searcherService, NotificationService notificationService)
    {
        Validate.notNull(accountEntity);
        Validate.notEmpty(searcherEntities);
        Validate.notNull(searcherService);
        Validate.notNull(notificationService);

        this.accountEntity = accountEntity;
        this.searcherService = searcherService;
        this.notificationService = notificationService;

        searcherEntitiesMap = new HashMap<>();
        for(SearcherEntity searcherEntity : searcherEntities){
            searcherEntitiesMap.put(searcherEntity.getId(), searcherEntity);
        }
    }

    @Override
    public void run()
    {
        if(log.isDebugEnabled()) {
            log.debug("Searcher task started for: " + getTaskInformation());
        }

        Map<UUID, List<SearchedItem>> newOrUpdatedItemsMap = new HashMap<>();

        for(SearcherEntity searcherEntity : searcherEntitiesMap.values())
        {
            try
            {
                List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);
                newOrUpdatedItemsMap.put(searcherEntity.getId(), newOrUpdatedItems);
            }
            catch (IOException e)
            {
                log.error(
                    "Failed to run searcher (id: '" +
                    searcherEntity.getId() + "', " +
                    "account-id: '" + accountEntity.getId() + "', " +
                    "mail: " + accountEntity.getMail() + "').",
                    e
                );
            }
        }

        for(Map.Entry<UUID, List<SearchedItem>> entry : newOrUpdatedItemsMap.entrySet())
        {
            if(entry.getValue() != null && !entry.getValue().isEmpty())
            {
                // TODO: combine all new or updated entities to the same message.
                SearcherEntity searcherEntity = searcherEntitiesMap.get(entry.getKey());
                notificationService.sendFoundNewItems(accountEntity, searcherEntity.getItemType(), entry.getValue());
            }
        }

        if(log.isDebugEnabled()) {
            log.debug("Searcher task compeleted successfully for: " + getTaskInformation());
        }
    }

    private String getTaskInformation()
    {
        return  "account-id: '" + accountEntity.getId() +
                "', mail: '" + accountEntity.getMail() +
                "', number of searchers: " + searcherEntitiesMap.size() +
                ", thread: '" + Thread.currentThread().getName() + "'";
    }
}
