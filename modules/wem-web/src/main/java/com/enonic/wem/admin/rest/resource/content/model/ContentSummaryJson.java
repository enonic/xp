package com.enonic.wem.admin.rest.resource.content.model;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.enonic.wem.admin.rest.resource.content.ContentImageUriResolver;
import com.enonic.wem.api.content.Content;

public class ContentSummaryJson
{
    private static final DateTimeFormatter isoDateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

    private final Content content;

    private final String iconUrl;

    public ContentSummaryJson( Content content )
    {
        this.content = content;
        this.iconUrl = ContentImageUriResolver.resolve( content );
    }
    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getId()
    {
        return content.getId() == null ? null : content.getId().toString();
    }

    public String getPath()
    {
        return content.getPath().toString();
    }

    public String getName()
    {
        return content.getName();
    }

    public String getType()
    {
        return content.getType() != null ? content.getType().toString() : null;
    }

    public String getDisplayName()
    {
        return content.getDisplayName();
    }

    public String getOwner()
    {
        return content.getOwner() != null ? content.getOwner().toString() : null;
    }

    public String getModifier()
    {
        return content.getModifier() != null ? content.getModifier().toString() : null;
    }

    public boolean isRoot()
    {
        return content.getPath().isRoot();
    }

    public String getModifiedTime()
    {
        return isoDateTimeFormatter.print( content.getModifiedTime() );
    }

    public String getCreatedTime()
    {
        return isoDateTimeFormatter.print( content.getCreatedTime() );
    }

}
