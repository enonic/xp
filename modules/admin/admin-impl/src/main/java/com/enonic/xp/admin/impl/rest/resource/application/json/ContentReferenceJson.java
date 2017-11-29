package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.content.Content;

public class ContentReferenceJson
{
    final String type;

    final String displayName;

    final String path;

    ContentReferenceJson( final Content content )
    {
        this.type = content.getType().toString();
        this.displayName = content.getDisplayName();
        this.path = content.getPath().toString();
    }

    public String getType()
    {
        return type;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getPath()
    {
        return path;
    }
}
