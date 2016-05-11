package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SellerType;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.function.Predicate;

public class TestSellerTypePredicate
{
    @Test
    public void testPredicateWhenAllowed()
    {
        // given
        Predicate<SearchedItem> predicate =
            new SellerTypePredicate(ItemType.Apartment, Collections.singleton(SellerType.Private), true /*allowNullValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withSellerType(SellerType.Private).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWhenNotAllowed()
    {
        // given
        Predicate<SearchedItem> predicate =
            new SellerTypePredicate(ItemType.Apartment, Collections.singleton(SellerType.Private), true /*allowNullValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withSellerType(SellerType.Brokerage).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(!result);
    }

    @Test
    public void testPredicateWhenNullSellerTypeAndNullAllowed()
    {
        // given
        Predicate<SearchedItem> predicate =
            new SellerTypePredicate(ItemType.Apartment, Collections.singleton(SellerType.Private), true /*allowNullValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withSellerType(null).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(result);
    }

    @Test
    public void testPredicateWhenNullSellerTypeAndNullNotAllowed()
    {
        // given
        Predicate<SearchedItem> predicate =
            new SellerTypePredicate(ItemType.Apartment, Collections.singleton(SellerType.Private), false /*allowNullValue*/);
        ApartmentItem apartmentItem = new ApartmentItemBuilder().withSellerType(null).build();

        // when
        boolean result = predicate.test(apartmentItem);

        // then
        Assert.isTrue(!result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPredicateWithNullItem()
    {
        // given
        Predicate<SearchedItem> predicate =
            new SellerTypePredicate(ItemType.Apartment, Collections.singleton(SellerType.Private), false /*allowNullValue*/);

        // when
        boolean result = predicate.test(null);

        // then - expected excepation
    }
}
