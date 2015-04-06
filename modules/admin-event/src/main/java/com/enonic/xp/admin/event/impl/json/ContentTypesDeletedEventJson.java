package com.enonic.xp.admin.event.impl.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypesDeletedEvent;

public class ContentTypesDeletedEventJson
    implements EventJson
{
    private final List<String> names;

    public ContentTypesDeletedEventJson( final ContentTypesDeletedEvent event )
    {
        names = new ArrayList<String>(  );
        for( ContentTypeName name : event.getNames() )
            names.add( name.toString() );
    }

    public List<String> getNames()
    {
        return names;
    }
}
