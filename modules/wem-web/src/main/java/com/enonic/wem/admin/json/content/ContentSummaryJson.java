package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.rest.resource.content.ContentImageUriResolver;
import com.enonic.wem.api.content.Content;

public class ContentSummaryJson extends ContentIdJson
{

    private final Content content;

    private final String iconUrl;

    public ContentSummaryJson( Content content )
    {
        super(content.getId());
        this.content = content;
        this.iconUrl = ContentImageUriResolver.resolve( content );
    }

    public String getIconUrl()
    {
        return iconUrl;
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
        return DateTimeFormatter.format( content.getModifiedTime() );
    }

    public String getCreatedTime()
    {
        return DateTimeFormatter.format( content.getCreatedTime() );
    }

    public boolean getHasChildren()
    {
        return content.hasChildren();
    }

}
