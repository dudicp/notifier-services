package com.patimer.notifier.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public abstract class ManagedEntity
{
    @Id
    @NotNull
    private UUID id;

    @NotNull
    private Date createdOn;

    @NotNull
    private Date modifiedOn;

    public ManagedEntity() {} // default constructor for creation

    public ManagedEntity(UUID id, Date createdOn, Date modifiedOn)
    {
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn()
    {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn)
    {
        this.modifiedOn = modifiedOn;
    }
}
