package com.patimer.notifier.controller;

import com.patimer.notifier.dto.AccountDto;
import com.patimer.notifier.dto.ChangePasswordRequestDto;
import com.patimer.notifier.service.AccountService;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.InvalidSessionException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/account")
public class AccountController
{
    private AccountService accountService;
    private SessionHandler sessionHandler;

    @Autowired
    public AccountController(AccountService accountService, SessionHandler sessionHandler)
    {
        Validate.notNull(accountService);
        Validate.notNull(sessionHandler);
        this.accountService = accountService;
        this.sessionHandler = sessionHandler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Account CRUD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value="/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody AccountDto create(@RequestBody AccountDto accountDto)
    {
        Validate.notNull(accountDto);
        return accountService.create(accountDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/current/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody AccountDto update(@RequestBody AccountDto accountDto, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountDto);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);

        if(!sessionPrincipal.getAccountId().equals(accountDto.getId())){
            throw new IllegalArgumentException(
                "Invalid id in account object, found '" +
                accountDto.getId() +
                "' but expected '" + sessionPrincipal.getAccountId() + "'."
            );
        }

        return accountService.update(accountDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/current/", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody AccountDto getCurrentLoggedOnAccount(Principal principal) throws InvalidSessionException
    {
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);

        return accountService.getById(sessionPrincipal.getAccountId());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/current/", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public void delete(Principal principal) throws InvalidSessionException
    {
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);

        accountService.delete(sessionPrincipal.getAccountId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Account OnBoarding
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value="/{id}/activate", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public void activate(@PathVariable("id") UUID accountId, @RequestParam("code") String activationCode)
    {
        Validate.notNull(accountId);
        Validate.notEmpty(activationCode);

        accountService.activate(accountId, activationCode);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/current/change-password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.ALL_VALUE)
    public void changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequest, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(changePasswordRequest);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);

        accountService.changePassword(sessionPrincipal.getAccountId(), changePasswordRequest);
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value="/forgot-password", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public void forgotPassword(@RequestParam("email") String emailAddress)
    {
        Validate.notEmpty(emailAddress);

        accountService.forgotPassword(emailAddress);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value="/{id}/unsubscribe", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public void unsubscribe(@PathVariable("id") UUID accountId, @RequestParam("code") String unsubscribeCode)
    {
        Validate.notNull(accountId);
        Validate.notEmpty(unsubscribeCode);

        accountService.unsubscribe(accountId, unsubscribeCode);
    }
}
