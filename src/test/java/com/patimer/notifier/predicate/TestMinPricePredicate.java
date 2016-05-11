package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.function.Predicate;

public class TestMinPricePredicate
{
    @Test
    public void testPredicateWhenPriceLowerThanMin()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 1000000, true /*allowNulValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withPrice(10000).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(!result);
    }

    @Test
    public void testPredicateWhenPriceHigherThanMin()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 2000000, true /*allowNulValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withPrice(3000000).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWhenPriceEqualToMin()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 2000000, true /*allowNulValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withPrice(2000000).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWhenNullPriceAndNullAllowed()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 2000000, true /*allowNulValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withPrice(null).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWhenNullPriceAndNullNotAllowed()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 2000000, false /*allowNulValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withPrice(null).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(!result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPredicateWithNullItem()
    {
        // given
        Predicate<SearchedItem> predicate = new MinPricePredicate(ItemType.Apartment, 2000000, false /*allowNulValue*/);

        // when
        boolean result = predicate.test(null);

        // then - expected excepation
    }
}
