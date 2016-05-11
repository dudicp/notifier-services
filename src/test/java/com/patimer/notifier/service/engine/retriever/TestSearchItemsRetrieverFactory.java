package com.patimer.notifier.service.engine.retriever;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SourceWebsiteEntity;
import com.patimer.notifier.model.SourceWebsiteEntityBuilder;
import com.patimer.notifier.model.SourceWebsiteType;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestSearchItemsRetrieverFactory
{
    private Set<ItemType> excludedItemTypes = new HashSet<>(Collections.singleton(ItemType.Motorcycle));
    private Set<SourceWebsiteType> excludedSourceWebsiteTypes = new HashSet<>(Collections.singleton(SourceWebsiteType.Yad2));

    @Test
    public void testCreate()
    {
        // given
        SearchItemsRetrieverFactory factory = new SearchItemsRetrieverFactory();

        ItemType[] allItemTypes = ItemType.values();
        SourceWebsiteType[] allWebSiteTypes = SourceWebsiteType.values();

        // when
        for(ItemType itemType : allItemTypes)
        {
            for(SourceWebsiteType sourceWebsiteType : allWebSiteTypes)
            {
                SourceWebsiteEntity sourceWebsiteEntity = new SourceWebsiteEntityBuilder().withWebsiteType(sourceWebsiteType).build();

                try
                {
                    SearchItemsRetriever searchItemsRetriever = factory.create(itemType, sourceWebsiteEntity);
                    Assert.notNull(searchItemsRetriever);
                }
                catch (IllegalArgumentException e)
                {
                    if(excludedItemTypes.contains(itemType) || excludedSourceWebsiteTypes.contains(sourceWebsiteType))
                    {
                        // ignore
                    }
                    else
                    {
                        throw new RuntimeException("test failed");
                    }
                }
            }
        }

        // then - no exeption.
    }
}
