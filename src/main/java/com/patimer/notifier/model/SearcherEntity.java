package com.patimer.notifier.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "searchers")
public class SearcherEntity extends ManagedEntity
{
    @NotNull
    private UUID accountId;

    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private PredicateEntity predicateEntity;

    @NotEmpty
    private Set<SourceWebsiteEntity> sourceWebsites = new HashSet<>();

    protected SearcherEntity(){} // default constructor required by mongodb

    public SearcherEntity(
        UUID accountId,
        String name,
        String description,
        PredicateEntity predicateEntity,
        Set<SourceWebsiteEntity> sourceWebsites
    )
    {
        super();
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.predicateEntity = predicateEntity;
        this.sourceWebsites = sourceWebsites;
    }

    public SearcherEntity(
        UUID id,
        Date createdOn,
        Date modifiedOn,
        UUID accountId,
        String name,
        String description,
        PredicateEntity predicateEntity,
        Set<SourceWebsiteEntity> sourceWebsites
    )
    {
        super(id, createdOn, modifiedOn);
        this.accountId = accountId;
        this.name = name;
        this.description = description;
        this.predicateEntity = predicateEntity;
        this.sourceWebsites = sourceWebsites;
    }

    public SearcherEntity(SearcherEntity other)
    {
        super(other.getId(), other.getCreatedOn(), other.getModifiedOn());
        this.accountId = other.getAccountId();
        this.name = other.getName();
        this.description = other.getDescription();
        this.predicateEntity = other.getPredicateEntity();
        this.sourceWebsites = new HashSet<>(other.getSourceWebsites());
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public PredicateEntity getPredicateEntity()
    {
        return predicateEntity;
    }

    public void setPredicateEntity(PredicateEntity predicateEntity)
    {
        this.predicateEntity = predicateEntity;
    }

    public Set<SourceWebsiteEntity> getSourceWebsites()
    {
        return sourceWebsites;
    }

    public void setSourceWebsites(Set<SourceWebsiteEntity> sourceWebsites)
    {
        this.sourceWebsites = sourceWebsites;
    }

    public void addSourceWebsite(SourceWebsiteEntity sourceWebsiteEntity)
    {
        if(sourceWebsites != null)
            sourceWebsites.add(sourceWebsiteEntity);
    }

    public void removeSourceWebsite(SourceWebsiteEntity sourceWebsiteEntity)
    {
        if(sourceWebsites != null)
            sourceWebsites.remove(sourceWebsiteEntity);
    }
}
