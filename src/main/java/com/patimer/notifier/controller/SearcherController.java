package com.patimer.notifier.controller;

import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.service.SearcherService;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.InvalidSessionException;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/account/{id}/searcher")
public class SearcherController
{
    private SearcherService searcherService;
    private SessionHandler sessionHandler;

    @Autowired
    public SearcherController(SearcherService searcherService, SessionHandler sessionHandler)
    {
        Validate.notNull(searcherService);
        Validate.notNull(sessionHandler);
        this.searcherService = searcherService;
        this.sessionHandler = sessionHandler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Account CRUD
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    SearcherDto create(@PathVariable("id") UUID accountId, @RequestBody SearcherDto searcherDto, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        Validate.notNull(searcherDto);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);
        verifyAccountIdInDto(searcherDto, sessionPrincipal);

        return searcherService.create(sessionPrincipal, searcherDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/{searcherId}/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    SearcherDto update(@PathVariable("id") UUID accountId, @PathVariable("searcherId") UUID searcherId, @RequestBody SearcherDto searcherDto, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        Validate.notNull(searcherId);
        Validate.notNull(searcherDto);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);
        verifyAccountIdInDto(searcherDto, sessionPrincipal);

        if(!searcherId.equals(searcherDto.getId())){
            throw new IllegalArgumentException(
                "Invalid id in searcher object, found '" +
                    searcherDto.getId() +
                    "' but expected '" + searcherId + "'."
            );
        }

        return searcherService.update(sessionPrincipal, searcherDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/{searcherId}/", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody SearcherDto getById(@PathVariable("id") UUID accountId, @PathVariable("searcherId") UUID searcherId, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        Validate.notNull(searcherId);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);

        return searcherService.getById(sessionPrincipal, searcherId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/{searcherId}/", method = RequestMethod.DELETE, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public void delete(@PathVariable("id") UUID accountId, @PathVariable("searcherId") UUID searcherId, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        Validate.notNull(searcherId);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);

        searcherService.delete(sessionPrincipal, searcherId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    List<SearcherDto> findAllForCurrentAccount(@PathVariable("id") UUID accountId, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);

        return searcherService.findByCurrentAccountId(sessionPrincipal);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Operation(s)
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/{searcherId}/perform", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void perform(@PathVariable("id") UUID accountId, @PathVariable("searcherId") UUID searcherId, Principal principal) throws InvalidSessionException
    {
        Validate.notNull(accountId);
        Validate.notNull(searcherId);
        SessionPrincipal sessionPrincipal = sessionHandler.verify(principal);
        verifyAccountIdInPathVariable(accountId, sessionPrincipal);

        searcherService.perform(sessionPrincipal, searcherId);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void verifyAccountIdInPathVariable(UUID accountIdInPathVariable, SessionPrincipal sessionPrincipal)
    {
        if(!sessionPrincipal.getAccountId().equals(accountIdInPathVariable)){
            throw new IllegalArgumentException(
                "Invalid id in path variable, found '" +
                    accountIdInPathVariable +
                    "' but expected '" + sessionPrincipal.getAccountId() + "'."
            );
        }
    }

    private void verifyAccountIdInDto(SearcherDto searcherDto, SessionPrincipal sessionPrincipal)
    {
        if(!sessionPrincipal.getAccountId().equals(searcherDto.getAccountId())){
            throw new IllegalArgumentException(
                "Invalid id in dto object, found '" +
                    searcherDto.getAccountId() +
                    "' but expected '" + sessionPrincipal.getAccountId() + "'."
            );
        }
    }
}
