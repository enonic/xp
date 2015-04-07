package com.enonic.xp.schema.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.event.Event;

public class ContentTypesUpdatedEvent
    implements Event
{
    private List<ContentTypeName> names;


    private final Instant modifiedTime;

    public ContentTypesUpdatedEvent( final Instant modifiedTime )
    {
        names = new ArrayList<ContentTypeName>();
        this.modifiedTime = modifiedTime;
    }

    public void addContentTypeName( final ContentTypeName name ) {
        names.add( name );
    }

    public List<ContentTypeName> getNames()
    {
        return names;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }
}
