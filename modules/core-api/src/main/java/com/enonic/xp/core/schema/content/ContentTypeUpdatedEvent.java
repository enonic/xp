package com.enonic.xp.core.schema.content;

import java.time.Instant;

import com.enonic.xp.core.event.Event;

public class ContentTypeUpdatedEvent
    implements Event
{
    private final ContentTypeName name;

    private final Instant modifiedTime;

    public ContentTypeUpdatedEvent( final ContentTypeName name, final Instant modifiedTime )
    {
        this.name = name;
        this.modifiedTime = modifiedTime;
    }

    public ContentTypeName getName()
    {
        return name;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }
}
