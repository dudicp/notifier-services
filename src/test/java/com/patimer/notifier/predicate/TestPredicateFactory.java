package com.patimer.notifier.predicate;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.PredicateEntity;
import com.patimer.notifier.model.PredicateEntityBuilder;
import com.patimer.notifier.model.SellerType;
import com.patimer.notifier.model.item.ApartmentItem;
import com.patimer.notifier.model.item.ApartmentItemBuilder;
import com.patimer.notifier.model.item.SearchedItem;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.function.Predicate;

public class TestPredicateFactory
{
    private PredicateFactory factory = new PredicateFactory();

    @Test
    public void testCreateWhenNoPredicates()
    {
        // given
        ItemType itemType = ItemType.Apartment;
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withAdditionalPredicates(null)
                .withMinPrice(null)
                .withMaxPrice(null)
                .withSellerType(null)
                .build();

        // when
        Predicate<SearchedItem> predicate = factory.createPredicate(itemType, predicateEntity);

        // then
        ApartmentItem apartmentItem = new ApartmentItemBuilder().build();
        Assert.isTrue(predicate.test(apartmentItem));
    }

    @Test
    public void testCreateWhenMaxPricePredicate()
    {
        // given
        ItemType itemType = ItemType.Apartment;
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withAdditionalPredicates(null)
                .withMinPrice(null)
                .withMaxPrice(2000000)
                .withSellerType(null)
                .build();

        // when
        Predicate<SearchedItem> predicate = factory.createPredicate(itemType, predicateEntity);

        // then
        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().withPrice(2000001).build();
        Assert.isTrue(!predicate.test(apartmentItem1));
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().withPrice(1000000).build();
        Assert.isTrue(predicate.test(apartmentItem2));
    }

    @Test
    public void testCreateWhenMinPricePredicate()
    {
        // given
        ItemType itemType = ItemType.Apartment;
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withAdditionalPredicates(null)
                .withMinPrice(1000000)
                .withMaxPrice(null)
                .withSellerType(null)
                .build();

        // when
        Predicate<SearchedItem> predicate = factory.createPredicate(itemType, predicateEntity);

        // then
        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().withPrice(1000).build();
        Assert.isTrue(!predicate.test(apartmentItem1));
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().withPrice(10000001).build();
        Assert.isTrue(predicate.test(apartmentItem2));
    }

    @Test
    public void testCreateWhenSellerTypePredicate()
    {
        // given
        ItemType itemType = ItemType.Apartment;
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withAdditionalPredicates(null)
                .withMinPrice(null)
                .withMaxPrice(null)
                .withSellerType(SellerType.Private)
                .build();

        // when
        Predicate<SearchedItem> predicate = factory.createPredicate(itemType, predicateEntity);

        // then
        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().withSellerType(SellerType.Brokerage).build();
        Assert.isTrue(!predicate.test(apartmentItem1));
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().withSellerType(SellerType.Private).build();
        Assert.isTrue(predicate.test(apartmentItem2));
    }


    @Test
    public void testCreateWhenMinAndMaxAndSellerTypePredicates()
    {
        // given
        ItemType itemType = ItemType.Apartment;
        PredicateEntity predicateEntity =
            new PredicateEntityBuilder()
                .withAdditionalPredicates(null)
                .withMinPrice(1000000)
                .withMaxPrice(2000000)
                .withSellerType(SellerType.Private)
                .build();

        // when
        Predicate<SearchedItem> predicate = factory.createPredicate(itemType, predicateEntity);

        // then
        ApartmentItem apartmentItem1 = new ApartmentItemBuilder().withPrice(1500000).withSellerType(SellerType.Brokerage).build();
        Assert.isTrue(!predicate.test(apartmentItem1));
        ApartmentItem apartmentItem2 = new ApartmentItemBuilder().withPrice(1500000).withSellerType(SellerType.Private).build();
        Assert.isTrue(predicate.test(apartmentItem2));
        ApartmentItem apartmentItem3 = new ApartmentItemBuilder().withPrice(1000).withSellerType(SellerType.Private).build();
        Assert.isTrue(!predicate.test(apartmentItem3));
        ApartmentItem apartmentItem4 = new ApartmentItemBuilder().withPrice(3000000).withSellerType(SellerType.Brokerage).build();
        Assert.isTrue(!predicate.test(apartmentItem4));
    }
}
