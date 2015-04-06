package com.enonic.xp.schema.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.event.Event;

public class ContentTypesDeletedEvent
    implements Event
{
    private List<ContentTypeName> names;

    public ContentTypesDeletedEvent( )
    {
        names = new ArrayList<ContentTypeName>();
    }

    public void addContentTypeName( final ContentTypeName name ) {
        names.add( name );
    }

    public List<ContentTypeName> getNames()
    {
        return names;
    }
}
