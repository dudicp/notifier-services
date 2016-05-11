package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This class represents the Data Transfer Object (DTO) of Thing in the system.
 * DTO is used to transfer objects between the UI/External API to the data model.
 */
public class SearcherDto extends ManagedObjectDto
{
    @NotNull
    private UUID accountId;

    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private PredicateDto predicate;

    @NotEmpty
    private Set<SourceWebsiteDto> sourceWebsites = new HashSet<>();

    public SearcherDto(){} // default constructor required by Jackson.

    public SearcherDto(
        UUID id,
        UUID accountId,
        String name,
        String description,
        PredicateDto predicate,
        Set<SourceWebsiteDto> sourceWebsites
    )
    {
        super(id);
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.predicate = predicate;
        this.sourceWebsites = sourceWebsites;
    }

    public SearcherDto(
        UUID id,
        Date createdOn,
        Date modifiedOn,
        UUID accountId,
        String name,
        String description,
        PredicateDto predicate,
        Set<SourceWebsiteDto> sourceWebsites
    )
    {
        super(id, createdOn, modifiedOn);
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.predicate = predicate;
        this.sourceWebsites = sourceWebsites;
    }

    @JsonProperty("accountId")
    public UUID getAccountId()
    {
        return accountId;
    }

    @JsonProperty("name")
    public String getName()
    {
        return name;
    }

    @JsonProperty("description")
    public String getDescription()
    {
        return description;
    }

    @JsonProperty("predicate")
    public PredicateDto getPredicate()
    {
        return predicate;
    }

    @JsonProperty("sourceWebsites")
    public Set<SourceWebsiteDto> getSourceWebsites()
    {
        return sourceWebsites;
    }
}
