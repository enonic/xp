package com.enonic.xp.content.event;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.event.Event;

public abstract class ContentEvent
    implements Event
{
    private ContentId id;

    public final ContentId getId()
    {
        return this.id;
    }

    public final void setId( final ContentId id )
    {
        this.id = id;
    }
}
