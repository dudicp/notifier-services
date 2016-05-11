package com.patimer.notifier.predicate;

import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.function.Predicate;

public class TestEmptyPredicate
{
    private Predicate<SearchedItem> predicate = new EmptyPredicate();

    @Test
    public void testPredicate()
    {
        // given
        ApartmentItem apartmentItem = new ApartmentItemBuilder().build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWithNullItem()
    {
        // given

        // when
        boolean result = predicate.test(null);

        // then
        Assert.isTrue(result);
    }
}
