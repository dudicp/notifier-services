package com.patimer.notifier.controller;


import com.patimer.notifier.dto.AccountDto;
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

}


