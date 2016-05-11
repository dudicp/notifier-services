package com.patimer.notifier.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class SourceWebsiteEntity
{
    @NotNull
    private SourceWebsiteType websiteType;

    @NotEmpty
    private String url;

    private Map<String, String> additionalData = new HashMap<>();

    protected SourceWebsiteEntity(){} // default constructor required by mongodb

    public SourceWebsiteEntity(
        SourceWebsiteType websiteType,
        String url,
        Map<String, String> additionalData
    )
    {
        this.websiteType = websiteType;
        this.url = url;
        this.additionalData = additionalData;
    }

    public SourceWebsiteType getWebsiteType()
    {
        return websiteType;
    }

    public void setWebsiteType(SourceWebsiteType websiteType)
    {
        this.websiteType = websiteType;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Map<String, String> getAdditionalData()
    {
        return additionalData;
    }

    public void setAdditionalData(Map<String, String> additionalData)
    {
        this.additionalData = additionalData;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceWebsiteEntity that = (SourceWebsiteEntity) o;

        if (websiteType != that.websiteType) return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode()
    {
        int result = websiteType != null ? websiteType.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
