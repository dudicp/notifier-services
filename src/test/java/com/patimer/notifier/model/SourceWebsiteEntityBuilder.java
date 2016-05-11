package com.patimer.notifier.model;

import java.util.HashMap;
import java.util.Map;

public class SourceWebsiteEntityBuilder
{
    private SourceWebsiteType websiteType = SourceWebsiteType.Madlan;
    private String url = "local/רחובות/רחובות%20המדע";
    private Map<String, String> additionalData = new HashMap<>();

    public SourceWebsiteEntityBuilder withWebsiteType(SourceWebsiteType websiteType)
    {
        this.websiteType = websiteType;
        return this;
    }

    public SourceWebsiteEntityBuilder withUrl(String url)
    {
        this.url = url;
        return this;
    }

    public SourceWebsiteEntityBuilder withAdditionalData(Map<String, String> additionalData)
    {
        this.additionalData = additionalData;
        return this;
    }

    public SourceWebsiteEntity build()
    {
        return new SourceWebsiteEntity(websiteType, url, additionalData);
    }
}
