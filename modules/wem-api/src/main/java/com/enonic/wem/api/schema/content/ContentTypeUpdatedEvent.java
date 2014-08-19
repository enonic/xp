package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.event.Event;

public class ContentTypeUpdatedEvent
    implements Event
{
    private final ContentTypeName name;

    public ContentTypeUpdatedEvent( final ContentTypeName name )
    {
        this.name = name;
    }

    public ContentTypeName getName()
    {
        return name;
    }


}
