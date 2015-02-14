package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.core.schema.content.ContentTypeDeletedEvent;

public class ContentTypeDeletedEventJson
    implements EventJson
{
    private final String name;

    public ContentTypeDeletedEventJson( final ContentTypeDeletedEvent event )
    {
        this.name = event.getName().toString();
    }

    public String getName()
    {
        return name;
    }
}
