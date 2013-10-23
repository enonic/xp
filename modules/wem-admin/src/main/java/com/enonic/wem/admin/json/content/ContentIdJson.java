package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.ContentId;

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
