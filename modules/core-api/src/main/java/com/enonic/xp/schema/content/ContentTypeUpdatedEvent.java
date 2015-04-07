package com.enonic.xp.schema.content;

import java.time.Instant;

import com.google.common.annotations.Beta;

import com.enonic.xp.event.Event;

@Beta
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
