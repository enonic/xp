package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.schema.content.ContentTypeDeletedEvent;

public class ContentTypeDeletedEventJson
    implements EventJson
{
    private final String name;

    public ContentTypeDeletedEventJson( final ContentTypeDeletedEvent event )
    {
        this.name = event.getName().toString();
    }

    public String getName()
    {
        return name;
    }
}
