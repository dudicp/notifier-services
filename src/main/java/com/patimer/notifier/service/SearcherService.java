package com.patimer.notifier.service;

import com.patimer.notifier.dto.SearcherDto;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface SearcherService
{
    SearcherDto create(SessionPrincipal sessionPrincipal, SearcherDto searcherDto);

    SearcherDto update(SessionPrincipal sessionPrincipal, SearcherDto searcherDto) throws NotFoundException; // must verify the searcher-id belongs to the user..

    void delete(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException; // must verify the searcher id belongs to the user.

    SearcherDto getById(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException;

    List<SearcherDto> findByCurrentAccountId(SessionPrincipal sessionPrincipal); //not must sessionPrincipal, must verify the searcher id belongs to the user.

    void perform(SessionPrincipal sessionPrincipal, UUID searcherId) throws NotFoundException;//not must sessionPrincipal, must verify the searcher id belongs to the user.
}
