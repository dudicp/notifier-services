package com.patimer.notifier.predicate;

import com.patimer.notifier.model.item.SearchedItem;

import java.util.function.Predicate;

public class EmptyPredicate implements Predicate<SearchedItem>
{
    @Override
    public boolean test(SearchedItem searchedItem)
    {
        return true;
    }
}
