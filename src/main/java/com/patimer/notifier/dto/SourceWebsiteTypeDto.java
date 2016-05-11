package com.patimer.notifier.dto;

import com.patimer.notifier.model.SourceWebsiteType;
import org.apache.commons.lang.Validate;

public enum  SourceWebsiteTypeDto
{
    Madlan,
    Yad2;

    public static SourceWebsiteTypeDto fromSourceWebsiteType(SourceWebsiteType websiteType)
    {
        Validate.notNull(websiteType);

        switch (websiteType)
        {
            case Madlan: return Madlan;
            case Yad2: return Yad2;
            default: throw new IllegalArgumentException("Unknown type: '" + websiteType + "'");
        }
    }

    public SourceWebsiteType toSourceWebsiteType()
    {
        switch (this)
        {
            case Madlan: return SourceWebsiteType.Madlan;
            case Yad2: return SourceWebsiteType.Yad2;
            default: throw new IllegalArgumentException("Unsupported type: '" + this + "'");
        }
    }
}
