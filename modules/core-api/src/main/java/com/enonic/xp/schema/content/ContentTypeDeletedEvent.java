package com.enonic.xp.schema.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.event.Event;

@Beta
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
