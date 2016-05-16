package com.patimer.notifier.controller;

import com.patimer.notifier.TestUtils;
import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.dto.SearcherDtoBuilder;
import com.patimer.notifier.service.SearcherService;
import com.patimer.notifier.service.SessionPrincipalBuilder;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * This test using Spring's full initialized Web-Application Context in order to reuse
 * the same configurations files as mentioned in the @ContextConfiguration annotation.
 *
 * The tests in this layer are testing the Controller behavior only using mock service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:unit-test-context.xml", "classpath:application-core-context.xml"})
@WebAppConfiguration
public class TestSearcherController
{
    private MockMvc mockMvc;

    @Autowired
    private SearcherService searcherServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp()
    {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        Mockito.reset(searcherServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(null).withAccountId(accountId).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.create(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/searcher/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(returnedSearcherDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testCreateWithNullAccountDto() throws Exception
    {
        // given
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.create(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);

        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(returnedSearcherDto.getAccountId()).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);


        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testCreateWhenAccountIdInDtoAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInDto = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(null).withAccountId(accountIdInDto).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.create(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountIdInDto + "/searcher/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testCreateWhenAccountIdInPathAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInPath = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(null).withAccountId(accountIdInSession).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.create(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountIdInPath + "/searcher/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testCreateWithNoPrincipal() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.create(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/searcher/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdate() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountId + "/searcher/" + searcherDto.getId() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(returnedSearcherDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testUpdateWhenAccountIdInDtoAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInDto = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountIdInDto).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountIdInDto + "/searcher/" + searcherDto.getId() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUpdateWhenAccountIdInPathAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInPath = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountIdInSession).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountIdInPath + "/searcher/" + searcherDto.getId() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUpdateWhenSearcherIdInDtoAndSearcherIdInPathIsDifferent() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherIdInDto = UUID.randomUUID();
        UUID searcherIdInPath = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(searcherIdInDto).withAccountId(accountId).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountId + "/searcher/" + searcherIdInPath + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUpdateWithNullSearcherDto() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherIdInPath = UUID.randomUUID();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);

        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(returnedSearcherDto.getAccountId()).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);


        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountId + "/searcher/" + searcherIdInPath + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUpdateWithNoPrincipal() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withAccountId(accountId).build();
        SearcherDto returnedSearcherDto = new SearcherDtoBuilder().build();
        when(searcherServiceMock.update(any(SessionPrincipal.class), any(SearcherDto.class))).thenReturn(returnedSearcherDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/" + accountId + "/searcher/" + searcherDto.getId() + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(searcherDto))
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  GetById
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetById() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(searcherId).withAccountId(accountId).build();
        when(searcherServiceMock.getById(any(SessionPrincipal.class), any(UUID.class))).thenReturn(searcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/" + accountId + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(searcherDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testGetByIdWithNoPrincipal() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(searcherId).withAccountId(accountId).build();
        when(searcherServiceMock.getById(any(SessionPrincipal.class), any(UUID.class))).thenReturn(searcherDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/" + accountId + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    @Test
    public void testGetByIdWhenAccountIdInPathAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInPath = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SearcherDto searcherDto = new SearcherDtoBuilder().withId(searcherId).withAccountId(accountIdInPath).build();
        when(searcherServiceMock.getById(any(SessionPrincipal.class), any(UUID.class))).thenReturn(searcherDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/" + accountIdInPath + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDelete() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountId + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testDeleteWhenNoPrincipal() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountId + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    @Test
    public void testDeleteWhenAccountIdInPathAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInPath = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountIdInPath + "/searcher/" + searcherId + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Perform
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPerform() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountId).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/searcher/" + searcherId + "/perform")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testPerformWithNoPrincipal() throws Exception
    {
        // given
        UUID accountId = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/searcher/" + searcherId + "/perform")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    @Test
    public void testPerformWhenAccountIdInPathAndSessionAccountIsDifferent() throws Exception
    {
        // given
        UUID accountIdInPath = UUID.randomUUID();
        UUID accountIdInSession = UUID.randomUUID();
        UUID searcherId = UUID.randomUUID();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountIdInSession).build();
        Principal principal = TestUtils.createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountIdInPath + "/searcher/" + searcherId + "/perform")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }
}
