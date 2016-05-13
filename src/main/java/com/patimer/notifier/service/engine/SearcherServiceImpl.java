package com.patimer.notifier.service.engine;

import com.patimer.notifier.dao.SearcherStoredDataDao;
import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.SearcherStoredDataEntity;
import com.patimer.notifier.model.item.SearchedItem;
import com.patimer.notifier.predicate.PredicateFactory;
import com.patimer.notifier.service.engine.retriever.SearchItemsRetriever;
import com.patimer.notifier.service.engine.retriever.SearchItemsRetrieverFactory;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SearcherServiceImpl implements SearcherService
{
    private final static Logger log = LogManager.getLogger(SearcherServiceImpl.class);

    private SearcherStoredDataDao searcherStoredDataDao;
    private SearchItemsRetrieverFactory searchItemsRetrieverFactory;
    private PredicateFactory predicateFactory;

    @Autowired
    public SearcherServiceImpl(
        SearcherStoredDataDao searcherStoredDataDao,
        SearchItemsRetrieverFactory searchItemsRetrieverFactory,
        PredicateFactory predicateFactory
    )
    {
        Validate.notNull(searcherStoredDataDao);
        Validate.notNull(searchItemsRetrieverFactory);
        Validate.notNull(predicateFactory);
        this.searcherStoredDataDao = searcherStoredDataDao;
        this.searchItemsRetrieverFactory = searchItemsRetrieverFactory;
        this.predicateFactory = predicateFactory;
    }

    public List<SearchedItem> searchNewOrUpdatedItems(SearcherEntity searcherEntity) throws IOException
    {
        Validate.notNull(searcherEntity);

        if(log.isDebugEnabled()){
            log.debug("Starting searcher worker (" + prettySearcherInfo(searcherEntity) + ")");
        }

        List<SearchItemsRetriever> searchItemsRetrievers =
            searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites());

        Predicate<SearchedItem> predicate =
            predicateFactory.createPredicate(searcherEntity.getItemType(), searcherEntity.getPredicateEntity());

        List<SearchedItem> allMatchedItems = new ArrayList<>();

        // 1. Retrieve items list from each searcher.
        for (SearchItemsRetriever searchItemsRetriever : searchItemsRetrievers)
        {
            // 1.1. retrieve items.
            List<SearchedItem> foundItems = searchItemsRetriever.retrieve();

            // 1.2. Filter items by predicates (by price, number of rooms, etc.).
            Stream<SearchedItem> foundItemsStream = foundItems.parallelStream();
            List<SearchedItem> matchedItems = foundItemsStream.filter(predicate).collect(Collectors.toList());

            allMatchedItems.addAll(matchedItems);
        }

        // 2. Load last stored items from the database.
        List<SearchedItem> storedItems = loadLastStoredSearchedItems(searcherEntity.getId());

        // 3. Find new or updated apartments.
        List<SearchedItem> newOrUpdatedItems = delta(storedItems, allMatchedItems);

        // 4. store all matched results in the database.
        storeMatchedSearchedItems(searcherEntity.getId(), searcherEntity.getItemType(), allMatchedItems);

        if(log.isDebugEnabled()) {
            log.debug(
                "Searcher worker completed successfully - found " +
                    ((newOrUpdatedItems != null)? newOrUpdatedItems.size() : 0) +
                    " new or updated items (" + prettySearcherInfo(searcherEntity) + ")."
            );
        }

        return newOrUpdatedItems;
    }

    private String prettySearcherInfo(SearcherEntity searcherEntity)
    {
        return  "(id: " + searcherEntity.getId() + "' " +
            "account-id: '" + searcherEntity.getAccountId() + "', " +
            "itemType: '" + searcherEntity.getItemType() + "').";
    }

    private List<SearchedItem> loadLastStoredSearchedItems(UUID searcherId)
    {
        List<SearchedItem> storedItems = new ArrayList<>();

        SearcherStoredDataEntity searcherStoredDataEntity = searcherStoredDataDao.findById(searcherId);
        if(searcherStoredDataEntity != null) {
            storedItems = searcherStoredDataEntity.getStoredItems();
        }

        return storedItems;
    }

    private void storeMatchedSearchedItems(UUID searcherId, ItemType itemType, List<SearchedItem> allMatchedItems)
    {
        SearcherStoredDataEntity entity =
            new SearcherStoredDataEntity(
                searcherId,
                Calendar.getInstance().getTime(),
                itemType,
                allMatchedItems
            );

        searcherStoredDataDao.upsert(entity);
    }

    private List<SearchedItem> delta(List<SearchedItem> storedItems, List<SearchedItem> foundItems)
    {
        List<SearchedItem> newOrUpdatedItems = new ArrayList<>();

        if(foundItems == null || foundItems.isEmpty()){
            return Collections.emptyList();
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
}
