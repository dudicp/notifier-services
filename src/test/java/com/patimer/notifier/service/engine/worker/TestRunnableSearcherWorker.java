package com.patimer.notifier.service.engine.worker;

import com.patimer.notifier.model.*;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import com.patimer.notifier.service.engine.SearcherService;
import com.patimer.notifier.service.notification.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestRunnableSearcherWorker
{
    @Mock
    private SearcherService searcherServiceMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Test
    public void testRunWhenOneSearcherAndFoundNewOrUpdatedItems() throws IOException
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        SearcherEntity searcherEntity = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).build();
        RunnableSearcherWorker runnableSearcherWorker =
            new RunnableSearcherWorker(
                accountEntity,
                Collections.singletonList(searcherEntity),
                searcherServiceMock,
                notificationServiceMock
            );

        ApartmentItem apartmentItem = new ApartmentItemBuilder().build();
        List<SearchedItem> newOrUpdatedItems = Collections.singletonList(apartmentItem);
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity)).thenReturn(newOrUpdatedItems);

        // when
        runnableSearcherWorker.run();

        // then
        verify(notificationServiceMock, times(1)).sendFoundNewItems(accountEntity, searcherEntity.getItemType(), newOrUpdatedItems);
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Test
    public void testRunWhenOneSearcherAndNoNewOrUpdatedItemsFound() throws IOException
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        SearcherEntity searcherEntity = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).build();
        RunnableSearcherWorker runnableSearcherWorker =
            new RunnableSearcherWorker(
                accountEntity,
                Collections.singletonList(searcherEntity),
                searcherServiceMock,
                notificationServiceMock
            );

        List<SearchedItem> newOrUpdatedItems = Collections.emptyList();
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity)).thenReturn(newOrUpdatedItems);

        // when
        runnableSearcherWorker.run();

        // then
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Test
    public void testRunWhenMultipleSearchersAndFoundNewOrUpdatedItemsForEachOne() throws IOException
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).withItemType(ItemType.Apartment).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).withItemType(ItemType.Apartment).build();

        RunnableSearcherWorker runnableSearcherWorker =
            new RunnableSearcherWorker(
                accountEntity,
                Arrays.asList(searcherEntity1, searcherEntity2),
                searcherServiceMock,
                notificationServiceMock
            );

        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().build();
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().build();
        List<SearchedItem> newOrUpdatedItems1 = Collections.singletonList(apartmentItem1);
        List<SearchedItem> newOrUpdatedItems2 = Collections.singletonList(apartmentItem2);
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity1)).thenReturn(newOrUpdatedItems1);
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity2)).thenReturn(newOrUpdatedItems2);

        // when
        runnableSearcherWorker.run();

        // then
        verify(notificationServiceMock, times(2)).sendFoundNewItems(any(AccountEntity.class), any(ItemType.class), anyList());
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Test
    public void testRunWhenMultipleSearchersAndOneFailed() throws IOException
    {
        // given
        AccountEntity accountEntity = new AccountEntityBuilder().build();
        SearcherEntity searcherEntity1 = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).withItemType(ItemType.Apartment).build();
        SearcherEntity searcherEntity2 = new SearcherEntityBuilder().withAccountId(accountEntity.getId()).withItemType(ItemType.Apartment).build();

        RunnableSearcherWorker runnableSearcherWorker =
            new RunnableSearcherWorker(
                accountEntity,
                Arrays.asList(searcherEntity1, searcherEntity2),
                searcherServiceMock,
                notificationServiceMock
            );

        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().build();
        List<SearchedItem> newOrUpdatedItems1 = Collections.singletonList(apartmentItem1);
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity1)).thenReturn(newOrUpdatedItems1);
        when(searcherServiceMock.searchNewOrUpdatedItems(searcherEntity2)).thenThrow(new IOException("unit-test"));

        // when
        runnableSearcherWorker.run();

        // then
        verify(notificationServiceMock, times(1)).sendFoundNewItems(accountEntity, searcherEntity1.getItemType(), newOrUpdatedItems1);
        verifyNoMoreInteractions(notificationServiceMock);
    }
}
