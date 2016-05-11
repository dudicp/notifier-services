package com.patimer.notifier.service.engine.retriever;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SourceWebsiteEntity;
import com.patimer.notifier.service.engine.retriever.madlan.MadlanApartmentSearchItemsRetrieverImpl;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SearchItemsRetrieverFactory
{
    public SearchItemsRetriever create(ItemType itemType, SourceWebsiteEntity websiteEntity)
    {
        Validate.notNull(itemType);
        Validate.notNull(websiteEntity);

        switch (itemType)
        {
            case Apartment:
                return createApartmentSearchItemsRetriever(websiteEntity);
            default:
                throw new IllegalArgumentException("Unsupported item type: '" + itemType + "'");
        }
    }

    private ApartmentSearchItemsRetriever createApartmentSearchItemsRetriever(SourceWebsiteEntity websiteEntity)
    {
        Validate.notNull(websiteEntity);

        switch (websiteEntity.getWebsiteType())
        {
            case Madlan:
                return createMadlanApartmentSearchItemsRetriever(websiteEntity);
            default:
                throw new IllegalArgumentException("Unsupported website type: '" + websiteEntity.getWebsiteType() + "'");
        }
    }

    public List<SearchItemsRetriever> create(ItemType itemType, Collection<SourceWebsiteEntity> websiteEntities)
    {
        Validate.notNull(itemType);
        Validate.notEmpty(websiteEntities);

        List<SearchItemsRetriever> result = new ArrayList<>();
        websiteEntities.forEach( websiteEntity -> result.add(create(itemType, websiteEntity)));
        return result;
    }

    private MadlanApartmentSearchItemsRetrieverImpl createMadlanApartmentSearchItemsRetriever(SourceWebsiteEntity websiteEntity)
    {
        return new MadlanApartmentSearchItemsRetrieverImpl(websiteEntity.getUrl());
    }
}
