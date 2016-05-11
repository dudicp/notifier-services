package com.patimer.notifier.service.converter;

import com.patimer.notifier.TestUtils;
import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.AccountDtoBuilder;
import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.AccountEntityBuilder;
import com.patimer.notifier.service.converter.AccountConverter;
import org.junit.Test;
import org.springframework.util.Assert;

public class TestAccountConverter
{
    private AccountConverter converter = new AccountConverter();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertForCreate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertForCreate()
    {
        // given
        AccountDto dto = new AccountDtoBuilder().build();

        // when
        AccountEntity entity = converter.convertForCreate(dto);

        // then
        Assert.notNull(entity);
        TestUtils.assertEqualsIncludeNull(dto.getMail(), entity.getMail());
        TestUtils.assertEqualsIncludeNull(dto.getName(), entity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getNotificationType().toNotificationType(), entity.getNotificationType());
        TestUtils.assertEqualsIncludeNull(dto.getPassword(), entity.getPassword());
        TestUtils.assertEqualsIncludeNull(dto.getPhone(), entity.getPhone());
    }

    @Test
    public void testConvertForCreateWithNullDto()
    {
        // given

        // when
        AccountEntity entity = converter.convertForCreate(null);

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
        AccountEntity entity = new AccountEntityBuilder().withMail("test1@test.com").withName("test1").build();
        AccountDto dto = new AccountDtoBuilder().withMail("test2@test.com").withName("test2").build();

        // when
        AccountEntity mergedEntity = converter.mergeForUpdate(dto, entity);

        // then
        Assert.notNull(entity);
        TestUtils.assertEqualsIncludeNull(dto.getName(), mergedEntity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getNotificationType().toNotificationType(), mergedEntity.getNotificationType());
        TestUtils.assertEqualsIncludeNull(dto.getPhone(), mergedEntity.getPhone());

        // attributes that should haven't been changed.
        TestUtils.assertEqualsIncludeNull(entity.getMail(), mergedEntity.getMail());
        TestUtils.assertEqualsIncludeNull(entity.getPassword(), mergedEntity.getPassword());
        TestUtils.assertEqualsIncludeNull(entity.getId(), mergedEntity.getId());
        TestUtils.assertEqualsIncludeNull(entity.getCreatedOn(), mergedEntity.getCreatedOn());
        TestUtils.assertEqualsIncludeNull(entity.getModifiedOn(), mergedEntity.getModifiedOn());
        TestUtils.assertEqualsIncludeNull(entity.getActivationCode(), mergedEntity.getActivationCode());
        TestUtils.assertEqualsIncludeNull(entity.getUnsubscribeCode(), mergedEntity.getUnsubscribeCode());
        TestUtils.assertEqualsIncludeNull(entity.getAccountState(), mergedEntity.getAccountState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeForUpdateWithNullDto()
    {
        // given
        AccountEntity entity = new AccountEntityBuilder().withMail("test1@test.com").withName("test1").build();

        // when
        converter.mergeForUpdate(null, entity);

        // then - expacted exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeForUpdateWithNullEntity()
    {
        // given
        AccountDto dto = new AccountDtoBuilder().withMail("test2@test.com").withName("test2").build();

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
        AccountEntity entity = new AccountEntityBuilder().build();

        // when
        AccountDto dto = converter.convertToDto(entity);

        // then
        Assert.notNull(dto);
        TestUtils.assertEqualsIncludeNull(dto.getMail(), entity.getMail());
        TestUtils.assertEqualsIncludeNull(dto.getName(), entity.getName());
        TestUtils.assertEqualsIncludeNull(dto.getNotificationType().toNotificationType(), entity.getNotificationType());
        TestUtils.assertEqualsIncludeNull(dto.getPhone(), entity.getPhone());
        Assert.isNull(dto.getPassword());
    }

    @Test
    public void testConvertToDtoWithNullEntity()
    {
        // given

        // when
        AccountDto dto = converter.convertToDto(null);

        // then
        Assert.isNull(dto);
    }
}
