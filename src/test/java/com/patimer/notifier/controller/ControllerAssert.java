package com.patimer.notifier.controller;


import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.PredicateDto;
import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.dto.SourceWebsiteDto;
import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

public class ControllerAssert
{
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
        MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    public static void isStatusOK(ResultActions resultActions) throws Exception
    {
        Assert.notNull(resultActions);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    public static void isStatusBadRequest(ResultActions resultActions) throws Exception
    {
        Assert.notNull(resultActions);
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    public static void isStatusUnauthorized(ResultActions resultActions) throws Exception
    {
        Assert.notNull(resultActions);
        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    public static void isStatusNotFound(ResultActions resultActions) throws Exception
    {
        Assert.notNull(resultActions);
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    public static void isJsonUtf8(ResultActions resultActions) throws Exception
    {
        Assert.notNull(resultActions);
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON_UTF8));
    }

    public static void isEquals(AccountDto accountDto, ResultActions resultActions, boolean ignorePassword) throws Exception
    {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("id", Matchers.is(accountDto.getId().toString())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("createdOn", Matchers.is(accountDto.getCreatedOn().getTime())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("modifiedOn", Matchers.is(accountDto.getModifiedOn().getTime())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("mail", Matchers.is(accountDto.getMail())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("name", Matchers.is(accountDto.getName())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("phone", Matchers.is(accountDto.getPhone())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("notificationType", Matchers.is(accountDto.getNotificationType().toString())));
        if(!ignorePassword)
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("password", Matchers.isEmptyOrNullString()));
    }

    public static void isEquals(SearcherDto searcherDto, ResultActions resultActions, boolean ignorePassword) throws Exception
    {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("id", Matchers.is(searcherDto.getId().toString())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("createdOn", Matchers.is(searcherDto.getCreatedOn().getTime())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("modifiedOn", Matchers.is(searcherDto.getModifiedOn().getTime())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("accountId", Matchers.is(searcherDto.getAccountId().toString())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("name", Matchers.is(searcherDto.getName())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("description", Matchers.is(searcherDto.getDescription())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("itemType", Matchers.is(searcherDto.getItemType().toString())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("predicate", Matchers.notNullValue()));
        isEqual(searcherDto.getPredicate(), resultActions);

        int index = 0;
        for(SourceWebsiteDto sourceWebsiteDto : searcherDto.getSourceWebsites()){
            isEqual(sourceWebsiteDto, resultActions, index);
        }
    }

    public static void isEqual(SourceWebsiteDto sourceWebsiteDto, ResultActions resultActions, int index) throws Exception
    {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("sourceWebsites[" + index + "].url", Matchers.is(sourceWebsiteDto.getUrl())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("sourceWebsites[" + index + "].type", Matchers.is(sourceWebsiteDto.getWebsiteType().toString())));
    }

    public static void isEqual(PredicateDto predicateDto, ResultActions resultActions) throws Exception
    {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("predicate.minPrice", Matchers.is(predicateDto.getMinPrice())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("predicate.maxPrice", Matchers.is(predicateDto.getMaxPrice())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("predicate.sellerType", Matchers.is(predicateDto.getSellerType().toString())));
    }

}


