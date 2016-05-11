package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.PredicateEntity;
import com.patimer.notifier.model.SellerType;
import com.patimer.notifier.model.item.SearchedItem;
import org.jsoup.helper.Validate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class PredicateFactory
{
    public Predicate<SearchedItem> createPredicate(ItemType itemType, PredicateEntity predicateEntity)
    {
        Validate.notNull(itemType);
        Validate.notNull(predicateEntity);

        List<Predicate<SearchedItem>> predicates = new ArrayList<>();

        if(predicateEntity.getMaxPrice() != null) {
            predicates.add(createMaxPricePredicate(itemType, predicateEntity.getMaxPrice()));
        }

        if(predicateEntity.getMinPrice() != null) {
            predicates.add(createMinPricePredicate(itemType, predicateEntity.getMinPrice()));
        }

        if(predicateEntity.getSellerType() != null) {
            predicates.add(createSellerTypePredicate(itemType, Collections.singleton(predicateEntity.getSellerType())));
        }

        if(predicateEntity.getAdditionalPredicates() != null) {
            for(Map.Entry<String, String> entry : predicateEntity.getAdditionalPredicates().entrySet())
            {
                predicates.add(createCustomPredicate(itemType, entry.getKey(), entry.getValue()));
            }
        }

        if(predicates.isEmpty()){
            return new EmptyPredicate();
        }
        else
        {
            Iterator<Predicate<SearchedItem>> iterator = predicates.iterator();
            Predicate<SearchedItem> result = iterator.next();
            while(iterator.hasNext())
            {
                result = result.and(iterator.next());
            }

            return result;
        }
    }

    private MaxPricePredicate createMaxPricePredicate(ItemType itemType, int max)
    {
        return new MaxPricePredicate(itemType, max, true /*allowNullValue*/);
    }

    private MinPricePredicate createMinPricePredicate(ItemType itemType, int min)
    {
        return new MinPricePredicate(itemType, min, true /*allowNullValue*/);
    }

    private SellerTypePredicate createSellerTypePredicate(ItemType itemType, Set<SellerType> allowedSellerTypes)
    {
        return new SellerTypePredicate(itemType, allowedSellerTypes, true /*allowNullValue*/);
    }

    private Predicate<SearchedItem> createCustomPredicate(ItemType itemType, String propertyName, String properyValue)
    {
        // TODO: implement
        return new EmptyPredicate();
    }
}
