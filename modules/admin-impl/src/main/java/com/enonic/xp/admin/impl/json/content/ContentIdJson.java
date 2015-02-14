package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.core.content.ContentId;

public class ContentIdJson
{
    private final String id;

    public ContentIdJson( final ContentId contentId )
    {
        this.id = contentId.toString();
    }

    public String getId()
    {
        return id;
    }
}
