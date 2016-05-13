package com.patimer.notifier.service.engine.retriever;

import com.patimer.notifier.dao.SearcherStoredDataDao;
import com.patimer.notifier.model.*;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import com.patimer.notifier.predicate.PredicateFactory;
import com.patimer.notifier.service.engine.SearcherService;
import com.patimer.notifier.service.engine.SearcherServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestSearcherService
{
    private SearcherService searcherService;

    @Mock
    private SearcherStoredDataDao searcherStoredDataDao;

    @Mock
    private SearchItemsRetrieverFactory searchItemsRetrieverFactory;

    @Before
    public void setUp()
    {
        PredicateFactory predicateFactory = new PredicateFactory();
        searcherService = new SearcherServiceImpl(searcherStoredDataDao, searchItemsRetrieverFactory, predicateFactory);
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenMatchedItemsFound() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(130).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(140).withLink("link4").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));

        SearcherStoredDataEntity searcherStoredDataEntity =
            new SearcherStoredDataEntityBuilder()
                .withSearcherId(searcherEntity.getId())
                .withItemType(ItemType.Apartment)
                .withLastSearchTime(Calendar.getInstance().getTime())
                .withStoredItems(Arrays.asList(item1, item3))
                .build();
        when(searcherStoredDataDao.findById(searcherEntity.getId())).thenReturn(searcherStoredDataEntity);

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.notNull(newOrUpdatedItems);
        Assert.isTrue(newOrUpdatedItems.size() == 1);
        Assert.isTrue(newOrUpdatedItems.iterator().next().getUniqueIdentifier().equals(item4.getUniqueIdentifier()));
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchNewOrUpdatedItemsWithNullSearcherEntity() throws IOException
    {
        // given

        // when
        searcherService.searchNewOrUpdatedItems(null);

        // then - expected exception
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenNoItemsFound() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));


        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(210).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(230).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(240).withLink("link4").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.isTrue(newOrUpdatedItems == null || newOrUpdatedItems.isEmpty());
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenNoMatchedItemsFound() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        when(searchItemsRetriever1.retrieve()).thenReturn(Collections.emptyList());
        when(searchItemsRetriever2.retrieve()).thenReturn(Collections.emptyList());

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.isTrue(newOrUpdatedItems == null || newOrUpdatedItems.isEmpty());
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenNoLastStoredItems() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(130).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(140).withLink("link4").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));

        when(searcherStoredDataDao.findById(searcherEntity.getId())).thenReturn(null);

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.notNull(newOrUpdatedItems);
        Assert.isTrue(newOrUpdatedItems.size() == 3);
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenFoundNewItems() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(130).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(140).withLink("link4").build();
        ApartmentItem item5 = new ApartmentItemBuilder().withPrice(140).withLink("link5").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));

        SearcherStoredDataEntity searcherStoredDataEntity =
            new SearcherStoredDataEntityBuilder()
                .withSearcherId(searcherEntity.getId())
                .withItemType(ItemType.Apartment)
                .withLastSearchTime(Calendar.getInstance().getTime())
                .withStoredItems(Collections.singletonList(item5))
                .build();
        when(searcherStoredDataDao.findById(searcherEntity.getId())).thenReturn(searcherStoredDataEntity);

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.notNull(newOrUpdatedItems);
        Assert.isTrue(newOrUpdatedItems.size() == 3);
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenFoundUpdatedItems() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(130).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(140).withLink("link4").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));

        ApartmentItem oldItem1 = new ApartmentItemBuilder(item1).withPrice(100).build();
        ApartmentItem oldItem3 = new ApartmentItemBuilder(item3).withPrice(100).build();
        ApartmentItem oldItem4 = new ApartmentItemBuilder(item4).withPrice(100).build();

        SearcherStoredDataEntity searcherStoredDataEntity =
            new SearcherStoredDataEntityBuilder()
                .withSearcherId(searcherEntity.getId())
                .withItemType(ItemType.Apartment)
                .withLastSearchTime(Calendar.getInstance().getTime())
                .withStoredItems(Arrays.asList(oldItem1, oldItem3, oldItem4))
                .build();
        when(searcherStoredDataDao.findById(searcherEntity.getId())).thenReturn(searcherStoredDataEntity);

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.notNull(newOrUpdatedItems);
        Assert.isTrue(newOrUpdatedItems.size() == 3);
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test
    public void testSearchNewOrUpdatedItemsWhenNoNewOrUpdatedItems() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();
        ApartmentItem item3 = new ApartmentItemBuilder().withPrice(130).withLink("link3").build();
        ApartmentItem item4 = new ApartmentItemBuilder().withPrice(140).withLink("link4").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenReturn(Arrays.asList(item3, item4));


        SearcherStoredDataEntity searcherStoredDataEntity =
            new SearcherStoredDataEntityBuilder()
                .withSearcherId(searcherEntity.getId())
                .withItemType(ItemType.Apartment)
                .withLastSearchTime(Calendar.getInstance().getTime())
                .withStoredItems(Arrays.asList(item1, item3, item4))
                .build();
        when(searcherStoredDataDao.findById(searcherEntity.getId())).thenReturn(searcherStoredDataEntity);

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then
        Assert.isTrue(newOrUpdatedItems == null || newOrUpdatedItems.isEmpty());
        verify(searcherStoredDataDao, times(1)).upsert(any(SearcherStoredDataEntity.class));
    }

    @Test(expected = IOException.class)
    public void testSearchNewOrUpdatedItemsWhenExceptionOccurredOnOneSearcher() throws IOException
    {
        // given
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withMinPrice(null)
                .withMaxPrice(200)
                .withSellerType(null)
                .withAdditionalPredicates(null)
                .build();

        SearcherEntity searcherEntity = new SearcherEntityBuilder().withPredicate(predicateEntity).build();
        SearchItemsRetriever searchItemsRetriever1 = mock(SearchItemsRetriever.class);
        SearchItemsRetriever searchItemsRetriever2 = mock(SearchItemsRetriever.class);
        when(searchItemsRetrieverFactory.create(searcherEntity.getItemType(), searcherEntity.getSourceWebsites()))
            .thenReturn(Arrays.asList(searchItemsRetriever1, searchItemsRetriever2));

        ApartmentItem item1 = new ApartmentItemBuilder().withPrice(110).withLink("link1").build();
        ApartmentItem item2 = new ApartmentItemBuilder().withPrice(320).withLink("link2").build();

        when(searchItemsRetriever1.retrieve()).thenReturn(Arrays.asList(item1, item2));
        when(searchItemsRetriever2.retrieve()).thenThrow(new IOException("unit-tests"));

        // when
        List<SearchedItem> newOrUpdatedItems = searcherService.searchNewOrUpdatedItems(searcherEntity);

        // then - expected exception
    }
}
