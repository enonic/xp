package com.enonic.xp.core.schema.content;

import com.enonic.xp.core.event.Event;

public class ContentTypeDeletedEvent
    implements Event
{
    private final ContentTypeName name;

    public ContentTypeDeletedEvent( final ContentTypeName name )
    {
        this.name = name;
    }

    public ContentTypeName getName()
    {
        return name;
    }
}
