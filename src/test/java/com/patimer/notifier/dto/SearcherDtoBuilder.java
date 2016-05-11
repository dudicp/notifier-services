package com.patimer.notifier.dto;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SearcherDtoBuilder extends ManagedObjectBuilder<SearcherDtoBuilder>
{
    private UUID accountId = UUID.randomUUID();
    private String name = "Ben-Gurion, Holon";
    private String description = null;
    private PredicateDto predicate = new PredicateDtoBuilder().build();
    private Set<SourceWebsiteDto> sourceWebsites = Collections.singleton(new SourceWebsiteDtoBuilder().build());

    public SearcherDtoBuilder withAccountId(UUID accountId)
    {
        this.accountId = accountId;
        return this;
    }

    public SearcherDtoBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public SearcherDtoBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    public SearcherDtoBuilder withPredicate(PredicateDto predicate)
    {
        this.predicate = predicate;
        return this;
    }

    public SearcherDtoBuilder withSourceWebsites(Set<SourceWebsiteDto> sourceWebsites)
    {
        this.sourceWebsites = sourceWebsites;
        return this;
    }

    public SearcherDto build()
    {
        return new SearcherDto(
            id,
            createdOn,
            modifiedOn,
            accountId,
            name,
            description,
            predicate,
            sourceWebsites
        );
    }
}
