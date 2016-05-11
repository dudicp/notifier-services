package com.patimer.notifier.model;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SearcherEntityBuilder extends ManagedEntityBuilder<SearcherEntityBuilder>
{
    private UUID accountId = UUID.randomUUID();
    private String name = "Ben-Gurion, Holon";
    private String description = null;
    private PredicateEntity predicateEntity = new PredicateEntityBuilder().build();
    private Set<SourceWebsiteEntity> sourceWebsites = Collections.singleton(new SourceWebsiteEntityBuilder().build());

    public SearcherEntityBuilder withAccountId(UUID accountId)
    {
        this.accountId = accountId;
        return this;
    }

    public SearcherEntityBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public SearcherEntityBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    public SearcherEntityBuilder withPredicate(PredicateEntity predicateEntity)
    {
        this.predicateEntity = predicateEntity;
        return this;
    }

    public SearcherEntityBuilder withSourceWebsites(Set<SourceWebsiteEntity> sourceWebsites)
    {
        this.sourceWebsites = sourceWebsites;
        return this;
    }

    public SearcherEntity build()
    {
        return new SearcherEntity(
            id,
            createdOn,
            modifiedOn,
            accountId,
            name,
            description,
            predicateEntity,
            sourceWebsites
        );
    }
}
