package com.enonic.xp.admin.event.impl.json;

import java.time.Instant;

import com.enonic.xp.core.schema.content.ContentTypeUpdatedEvent;

public class ContentTypeUpdatedEventJson
    implements EventJson
{
    private final String name;

    private final Instant modifiedTime;

    public ContentTypeUpdatedEventJson( final ContentTypeUpdatedEvent event )
    {
        this.name = event.getName().toString();
        this.modifiedTime = event.getModifiedTime();
    }

    public String getName()
    {
        return name;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }
}
