package com.patimer.notifier.service.engine;

import com.patimer.notifier.dao.SearcherStoredDataDao;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.SearcherStoredDataEntity;
import com.patimer.notifier.model.item.SearchedItem;
import com.patimer.notifier.service.engine.retriever.SearchItemsRetriever;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearcherWorker
{
    private final static Logger log = LogManager.getLogger(SearcherWorker.class);

    private List<SearchItemsRetriever> searchers;
    private Predicate<SearchedItem> predicate;
    private SearcherEntity searcherEntity;
    private String mailAddress;
    private SearcherStoredDataDao searcherStoredDataDao;
    private List<SearcherListener> registeredListeners = new ArrayList<>();

    public SearcherWorker(
        List<SearchItemsRetriever> searchers,
        Predicate<SearchedItem> predicate,
        SearcherEntity searcherEntity,
        String mailAddress,
        SearcherStoredDataDao searcherStoredDataDao
    )
    {
        Validate.notEmpty(searchers);
        Validate.notNull(predicate);
        Validate.notNull(searcherEntity);
        Validate.notEmpty(mailAddress);
        Validate.notNull(searcherStoredDataDao);

        this.searchers = searchers;
        this.predicate = predicate;
        this.searcherEntity = searcherEntity;
        this.mailAddress = mailAddress;
        this.searcherStoredDataDao = searcherStoredDataDao;
    }

    public SearcherWorker(
        List<SearchItemsRetriever> searchers,
        Predicate<SearchedItem> predicate,
        SearcherEntity searcherEntity,
        String mailAddress,
        SearcherStoredDataDao searcherStoredDataDao,
        List<SearcherListener> registeredListeners
    )
    {
        Validate.notEmpty(searchers);
        Validate.notNull(predicate);
        Validate.notNull(searcherEntity);
        Validate.notEmpty(mailAddress);
        Validate.notNull(searcherStoredDataDao);

        this.searchers = searchers;
        this.predicate = predicate;
        this.searcherEntity = searcherEntity;
        this.mailAddress = mailAddress;
        this.searcherStoredDataDao = searcherStoredDataDao;
        this.registeredListeners = registeredListeners;
    }

    public void registerListener(SearcherListener listener)
    {
        Validate.notNull(listener);

        if(registeredListeners == null)
            registeredListeners = new ArrayList<>();

        registeredListeners.add(listener);
    }

    public void unregisterListener(SearcherListener listener)
    {
        Validate.notNull(listener);

        if(registeredListeners != null)
            registeredListeners.remove(listener);
    }

    public void run()
    {
        if(log.isDebugEnabled()){
            log.debug("Starting searcher worker (" + prettySearcherInfo() + ")");
        }

        //List<Searcher> searchers = searcherFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites());
        //Predicate<SearchedItem> predicate = predicateFactory.createPredicate(searcherEntity.getItemType(), searcherEntity.getPredicateEntity());

        List<SearchedItem> allMatchedItems = new ArrayList<>();

        // 1. Retrieve items list from each searcher.
        for (SearchItemsRetriever searcher : searchers)
        {
            try
            {
                // 1.1. retrieve items.
                List<SearchedItem> foundItems = searcher.retrieve();

                // 1.2. Filter items by predicates (by price, number of rooms, etc.).
                Stream<SearchedItem> foundItemsStream = foundItems.parallelStream();
                List<SearchedItem> matchedItems = foundItemsStream.filter(predicate).collect(Collectors.toList());

                allMatchedItems.addAll(matchedItems);
            }
            catch (IOException e)
            {
                // ignore and continue
                log.error(
                    "Failed to retrieve data from searcher: '" +
                        searcher.getWebsiteType() + "' " +
                        "(" + prettySearcherInfo() + ")."
                    , e);
            }
        }

        // 2. Load last stored items from the database.
        List<SearchedItem> storedItems = loadLastStoredSearchedItems();

        // 3. Find new or updated apartments.
        List<SearchedItem> newOrUpdatedItems = delta(storedItems, allMatchedItems);

        // 4. store all matched results in the database.
        storeMatchedSearchedItems(allMatchedItems);

        // 5. Notify
        notify(newOrUpdatedItems);

        if(log.isDebugEnabled()) {
            log.debug(
                "Searcher worker completed successfully - found " +
                    ((newOrUpdatedItems != null)? newOrUpdatedItems.size() : 0) +
                    " new or updated items (" + prettySearcherInfo() + ")."
            );
        }
    }

    private String prettySearcherInfo()
    {
        return  "(id: " + searcherEntity.getId() + "' " +
                "owner: '" + mailAddress + "', " +
                "account-id: '" + searcherEntity.getAccountId() + "', " +
                "itemType: '" + searcherEntity.getItemType() + "').";
    }

    private List<SearchedItem> loadLastStoredSearchedItems()
    {
        List<SearchedItem> storedItems = new ArrayList<>();

        SearcherStoredDataEntity searcherStoredDataEntity = searcherStoredDataDao.findById(searcherEntity.getId());
        if(searcherStoredDataEntity != null) {
            storedItems = searcherStoredDataEntity.getStoredItems();
        }

        return storedItems;
    }

    private void storeMatchedSearchedItems(List<SearchedItem> allMatchedItems)
    {
        SearcherStoredDataEntity entity =
            new SearcherStoredDataEntity(
                searcherEntity.getId(),
                Calendar.getInstance().getTime(),
                searcherEntity.getItemType(),
                allMatchedItems
            );

        searcherStoredDataDao.upsert(entity);
    }

    private List<SearchedItem> delta(List<SearchedItem> storedItems, List<SearchedItem> foundItems)
    {
        List<SearchedItem> newOrUpdatedItems = new ArrayList<>();

        if(foundItems == null || foundItems.isEmpty()){
            return new ArrayList<>();
        }

        if(storedItems == null || storedItems.isEmpty()) {
            return foundItems;
        }

        Map<String, SearchedItem> storedItemsAsMap = new HashMap<>();
        storedItems.forEach(
            searchedItem -> storedItemsAsMap.put(searchedItem.getUniqueIdentifier().toLowerCase(), searchedItem)
        );

        for(SearchedItem item : foundItems)
        {
            if(!storedItemsAsMap.containsKey(item.getUniqueIdentifier().toLowerCase()))
            {
                // new item
                newOrUpdatedItems.add(item);
            }
            else
            {
                // existing item - need to add only if something has been changed.
                SearchedItem storedItem = storedItemsAsMap.get(item.getUniqueIdentifier().toLowerCase());
                if(!storedItem.equals(item)){
                    newOrUpdatedItems.add(item);
                }
            }
        }

        return newOrUpdatedItems;
    }

    private void notify(List<SearchedItem> newOrUpdatedItems)
    {
        if(registeredListeners != null && newOrUpdatedItems != null && newOrUpdatedItems.size() > 0)
        {
            for(SearcherListener listener : registeredListeners)
            {
                listener.notify(searcherEntity, mailAddress, newOrUpdatedItems);
            }
        }
    }
}
