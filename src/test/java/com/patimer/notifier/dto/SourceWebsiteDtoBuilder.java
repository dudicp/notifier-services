package com.patimer.notifier.dto;

public class SourceWebsiteDtoBuilder
{
    private SourceWebsiteTypeDto websiteType = SourceWebsiteTypeDto.Madlan;
    private String url = "local/רחובות/רחובות%20המדע";

    public SourceWebsiteDtoBuilder withWebsiteType(SourceWebsiteTypeDto websiteType)
    {
        this.websiteType = websiteType;
        return this;
    }

    public SourceWebsiteDtoBuilder withUrl(String url)
    {
        this.url = url;
        return this;
    }

    public SourceWebsiteDto build()
    {
        return new SourceWebsiteDto(websiteType, url);
    }
}
