package com.patimer.notifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * This class represents the Data Transfer Object (DTO) of Thing in the system.
 * DTO is used to transfer objects between the UI/External API to the data model.
 */
public class SourceWebsiteDto
{
    @NotNull
    private SourceWebsiteTypeDto websiteType;

    @NotEmpty
    private String url;

    protected SourceWebsiteDto(){} // default constructor required by mongodb

    public SourceWebsiteDto(SourceWebsiteTypeDto websiteType, String url)
    {
        this.websiteType = websiteType;
        this.url = url;
    }

    @JsonProperty("type")
    public SourceWebsiteTypeDto getWebsiteType()
    {
        return websiteType;
    }

    @JsonProperty("url")
    public String getUrl()
    {
        return url;
    }
}
