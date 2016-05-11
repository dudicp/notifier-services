package com.patimer.notifier.model;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public abstract class ManagedEntityBuilder<T>
{
    protected UUID id = UUID.randomUUID();
    protected Date createdOn = Calendar.getInstance().getTime();
    protected Date modifiedOn = Calendar.getInstance().getTime();


    public T withId(UUID id)
    {
        this.id = id;
        return (T)this;
    }

    public T withCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
        return (T)this;
    }

    public T withModifiedOn(Date modifiedOn)
    {
        this.modifiedOn = modifiedOn;
        return (T)this;
    }
}
