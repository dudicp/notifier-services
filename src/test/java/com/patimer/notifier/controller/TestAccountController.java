package com.patimer.notifier.controller;

import com.patimer.notifier.TestUtils;
import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.AccountDtoBuilder;
import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.dto.ChangePasswordRequestDtoBuilder;
import com.patimer.notifier.service.AccountService;
import com.patimer.notifier.service.SessionPrincipalBuilder;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import static org.mockito.Mockito.doThrow;
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
public class TestAccountController
{
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp()
    {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        Mockito.reset(accountServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Create
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        AccountDto accountDto = new AccountDtoBuilder().withId(null).withMail(mail).build();
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.create(any(AccountDto.class))).thenReturn(returnedAccountDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(accountDto))
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(returnedAccountDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testCreateWithNullAccountDto() throws Exception
    {
        // given
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.create(any(AccountDto.class))).thenReturn(returnedAccountDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdate() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        AccountDto accountDto = new AccountDtoBuilder().withMail(mail).build();
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.update(any(AccountDto.class))).thenReturn(returnedAccountDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withAccountId(accountDto.getId()).withMail(mail).build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(accountDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(returnedAccountDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testUpdateWhenAccountDtoNotBelongToSessionPrincipal() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        AccountDto accountDto = new AccountDtoBuilder().withMail(mail).build();
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.update(any(AccountDto.class))).thenReturn(returnedAccountDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(accountDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUpdateWithNullAccountDto() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.update(any(AccountDto.class))).thenReturn(returnedAccountDto);
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/")
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
        String mail = "cesc@gunners.com";
        AccountDto accountDto = new AccountDtoBuilder().withId(null).withMail(mail).build();
        AccountDto returnedAccountDto = new AccountDtoBuilder().build();
        when(accountServiceMock.update(any(AccountDto.class))).thenReturn(returnedAccountDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(accountDto))
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Get Current Logged On Account
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetCurrentLoggedOnAccount() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        AccountDto accountDto = new AccountDtoBuilder().withId(sessionPrincipal.getAccountId()).withMail(mail).build();
        when(accountServiceMock.getById(sessionPrincipal.getAccountId())).thenReturn(accountDto);
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/current/")
                    .principal(principal)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
        ControllerAssert.isJsonUtf8(resultActions);
        ControllerAssert.isEquals(accountDto, resultActions, true /*ignorePassword*/);
    }

    @Test
    public void testGetCurrentLoggedOnAccountWithNoPrincipal() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        AccountDto accountDto = new AccountDtoBuilder().withId(sessionPrincipal.getAccountId()).withMail(mail).build();
        when(accountServiceMock.getById(sessionPrincipal.getAccountId())).thenReturn(accountDto);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/current/")
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    @Test
    public void testGetCurrentLoggedOnAccountWithInvalidPrincipal() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        AccountDto accountDto = new AccountDtoBuilder().withId(sessionPrincipal.getAccountId()).withMail(mail).build();
        when(accountServiceMock.getById(sessionPrincipal.getAccountId())).thenReturn(accountDto);
        Principal principal = new UsernamePasswordAuthenticationToken("string", null /*credentials*/);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/current/")
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    // not sure if we should have such test because in real life the existence of session meaning the existence of
    // the account.
    @Test
    public void testGetCurrentLoggedOnAccountWhenNotFound() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        when(accountServiceMock.getById(sessionPrincipal.getAccountId())).thenThrow(new NotFoundException("unit-test"));
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .get("/account/current/")
                    .principal(principal)
                    .accept(MediaType.APPLICATION_JSON)
            );

        // then
        ControllerAssert.isStatusNotFound(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Delete
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testDelete() throws Exception
    {
        // given
        String mail = "cesc@gunners.com";
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withMail(mail).build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/current/")
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testDeleteWithNoPrincipal() throws Exception
    {
        // given

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/current/")
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Activate
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testActivate() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();
        String activateionCode = UUID.randomUUID().toString();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/activate")
                    .param("code", activateionCode)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testActivateWithNoCode() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/activate")
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testActivateWithEmptyCode() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();
        String activateionCode = "";

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/" + accountId + "/activate")
                    .param("code", activateionCode)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Change Password
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testChangePassword() throws Exception
    {
        // given
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().build();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(changePasswordRequestDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testChangePasswordWithNoChangePasswordRequest() throws Exception
    {
        // given
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();
        Principal principal = createPrincipal(sessionPrincipal);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testChangePasswordWithNoPrincipal() throws Exception
    {
        // given
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().build();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(changePasswordRequestDto))
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    @Test
    public void testChangePasswordWhenBadCredentials() throws Exception
    {
        // given
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDtoBuilder().build();
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().build();
        Principal principal = createPrincipal(sessionPrincipal);
        doThrow(new BadCredentialsException("unit-tests")).when(accountServiceMock).changePassword(any(UUID.class), any(ChangePasswordRequestDto.class));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .put("/account/current/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(changePasswordRequestDto))
                    .principal(principal)
            );

        // then
        ControllerAssert.isStatusUnauthorized(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Forgot Password
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testForgotPassword() throws Exception
    {
        // given
        String mail = "cesc@gmail.com";

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/forgot-password")
                    .param("email", mail)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testForgotPasswordWithNoEmail() throws Exception
    {
        // given

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/forgot-password")
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testForgotPasswordWithEmptyEmail() throws Exception
    {
        // given
        String mail = "";

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .post("/account/forgot-password")
                    .param("email", mail)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Unsubscribe
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUnsubscribe() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();
        String unsubscribeCode = UUID.randomUUID().toString();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountId + "/unsubscribe")
                    .param("code", unsubscribeCode)
            );

        // then
        ControllerAssert.isStatusOK(resultActions);
    }

    @Test
    public void testUnsubscribeWithNoCode() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountId + "/unsubscribe")
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    @Test
    public void testUnsubscribeWithEmptyCode() throws Exception
    {
        // given
        String accountId = UUID.randomUUID().toString();
        String unsubscribeCode = "";

        // when
        ResultActions resultActions =
            mockMvc.perform(
                MockMvcRequestBuilders
                    .delete("/account/" + accountId + "/unsubscribe")
                    .param("code", unsubscribeCode)
            );

        // then
        ControllerAssert.isStatusBadRequest(resultActions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Principal createPrincipal(SessionPrincipal sessionPrincipal)
    {
        return new UsernamePasswordAuthenticationToken(sessionPrincipal, null /*credentials*/);
    }
}
