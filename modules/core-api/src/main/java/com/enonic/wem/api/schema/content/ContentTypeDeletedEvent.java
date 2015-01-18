package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.event.Event;

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
