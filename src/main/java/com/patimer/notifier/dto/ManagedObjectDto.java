package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public abstract class ManagedObjectDto
{
    @NotNull
    private UUID id;

    private Date createdOn;

    private Date modifiedOn;

    public ManagedObjectDto(){} // default constructor needed for create operations.

    public ManagedObjectDto(UUID id)
    {
        this.id = id;
    }

    public ManagedObjectDto(UUID id, Date createdOn, Date modifiedOn)
    {
        this.id = id;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("id")
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    @JsonProperty("createdOn")
    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    @JsonProperty("modifiedOn")
    public Date getModifiedOn()
    {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn)
    {
        this.modifiedOn = modifiedOn;
    }
}
