package com.patimer.notifier.service.converter;

import com.patimer.notifier.TestUtils;
import com.patimer.notifier.dto.PredicateDto;
import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.dto.SearcherDtoBuilder;
import com.patimer.notifier.dto.SourceWebsiteDto;
import com.patimer.notifier.model.PredicateEntity;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.SearcherEntityBuilder;
import com.patimer.notifier.model.SourceWebsiteEntity;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Set;

public class TestSearcherConverter
{
    private SearcherConverter converter = new SearcherConverter();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertForCreate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertForCreate()
    {
        // given
        SearcherDto dto = new SearcherDtoBuilder().build();

        // when
        SearcherEntity entity = converter.convertForCreate(dto);

        // then
        Assert.notNull(entity);
        TestUtils.assertEqualsIncludeNull(dto.getAccountId(), entity.getAccountId());
        TestUtils.assertEqualsIncludeNull(dto.getName(), entity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getDescription(), entity.getDescription());
        TestUtils.assertEqualsIncludeNull(dto.getItemType().toItemType(), entity.getItemType());
        assertPredicateEquals(dto.getPredicate(), entity.getPredicateEntity());
        assertSourceWebsiteEquals(dto.getSourceWebsites(), entity.getSourceWebsites());
    }

    @Test
    public void testConvertForCreateWithNullDto()
    {
        // given

        // when
        SearcherEntity entity = converter.convertForCreate(null);

        // then
        Assert.isNull(entity);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // MergeForUpdate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testMergeForUpdate()
    {
        // given
        SearcherEntity entity = new SearcherEntityBuilder().withName("test1").build();
        SearcherDto dto = new SearcherDtoBuilder().withName("test2").build();

        // when
        SearcherEntity mergedEntity = converter.mergeForUpdate(dto, entity);

        // then
        Assert.notNull(entity);
        TestUtils.assertEqualsIncludeNull(dto.getName(), mergedEntity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getDescription(), mergedEntity.getDescription());
        assertPredicateEquals(dto.getPredicate(), mergedEntity.getPredicateEntity());
        assertSourceWebsiteEquals(dto.getSourceWebsites(), mergedEntity.getSourceWebsites());

        // attributes that should haven't been changed.
        TestUtils.assertEqualsIncludeNull(entity.getId(), mergedEntity.getId());
        TestUtils.assertEqualsIncludeNull(entity.getCreatedOn(), mergedEntity.getCreatedOn());
        TestUtils.assertEqualsIncludeNull(entity.getModifiedOn(), mergedEntity.getModifiedOn());
        TestUtils.assertEqualsIncludeNull(entity.getAccountId(), mergedEntity.getAccountId());
        TestUtils.assertEqualsIncludeNull(entity.getItemType(), mergedEntity.getItemType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeForUpdateWithNullDto()
    {
        // given
        SearcherEntity entity = new SearcherEntityBuilder().withName("test1").build();

        // when
        converter.mergeForUpdate(null, entity);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeForUpdateWithNullEntity()
    {
        // given
        SearcherDto dto = new SearcherDtoBuilder().withName("test2").build();

        // when
        converter.mergeForUpdate(dto, null);

        // then - expacted exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertToDto
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertToDto()
    {
        // given
        SearcherEntity entity = new SearcherEntityBuilder().build();

        // when
        SearcherDto dto = converter.convertToDto(entity);

        // then
        Assert.notNull(dto);
        TestUtils.assertEqualsIncludeNull(dto.getAccountId(), entity.getAccountId());
        TestUtils.assertEqualsIncludeNull(dto.getName(), entity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getDescription(), entity.getDescription());
        TestUtils.assertEqualsIncludeNull(dto.getItemType().toItemType(), entity.getItemType());
        assertPredicateEquals(dto.getPredicate(), entity.getPredicateEntity());
        assertSourceWebsiteEquals(dto.getSourceWebsites(), entity.getSourceWebsites());
    }

    @Test
    public void testConvertToDtoWithNullEntity()
    {
        // given

        // when
        SearcherDto dto = converter.convertToDto((SearcherEntity)null);

        // then
        Assert.isNull(dto);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void assertPredicateEquals(PredicateDto dto, PredicateEntity entity)
    {
        if(dto == null && entity == null)
            return;

        if(dto != null && entity!= null)
        {
            TestUtils.assertEqualsIncludeNull(dto.getMinPrice(), entity.getMinPrice());
            TestUtils.assertEqualsIncludeNull(dto.getMaxPrice(), entity.getMaxPrice());
            Assert.isTrue(  (dto.getSellerType() == null && entity.getSellerType() == null) ||
                (dto.getSellerType().toSellerType() == entity.getSellerType())  );
        }
        else
        {
            // one of them is null
            Assert.isTrue(false);
        }
    }

    private void assertSourceWebsiteEquals(SourceWebsiteDto dto, SourceWebsiteEntity entity)
    {
        if(dto == null && entity == null)
            return;

        if(dto != null && entity!= null)
        {
            TestUtils.assertEqualsIncludeNull(dto.getUrl(), entity.getUrl());
            Assert.isTrue(  (dto.getWebsiteType() == null && entity.getWebsiteType() == null) ||
                (dto.getWebsiteType().toSourceWebsiteType() == entity.getWebsiteType())  );
        }
        else
        {
            // one of them is null
            Assert.isTrue(false);
        }
    }

    private void assertSourceWebsiteEquals(Set<SourceWebsiteDto> dtos, Set<SourceWebsiteEntity> entities)
    {
        if(dtos == null && entities == null)
            return;

        if(dtos != null && entities!= null)
        {
            Assert.isTrue(dtos.size() == entities.size());

            for(SourceWebsiteDto dto : dtos)
            {
                boolean found = false;

                for(SourceWebsiteEntity entity : entities)
                {
                    if((entity.getUrl() == null && dto.getUrl() == null) || (entity.getUrl().equalsIgnoreCase(dto.getUrl()))){
                        assertSourceWebsiteEquals(dto, entity);
                        found = true;
                        break;
                    }
                }

                Assert.isTrue(found);
            }
        }
        else
        {
            // one of them is null
            Assert.isTrue(false);
        }
    }
}
