package com.enonic.xp.admin.event.impl.json;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypesUpdatedEvent;

public class ContentTypesUpdatedEventJson
    implements EventJson
{
    private final List<String> names;

    private final Instant modifiedTime;

    public ContentTypesUpdatedEventJson( final ContentTypesUpdatedEvent event )
    {
        names = new ArrayList<String>(  );
        for( ContentTypeName name : event.getNames() )
            names.add( name.toString() );
        this.modifiedTime = event.getModifiedTime();
    }

    public List<String> getNames()
    {
        return names;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }
}
