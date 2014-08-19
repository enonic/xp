package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.schema.content.ContentTypeUpdatedEvent;

public class ContentTypeUpdatedEventJson
    implements EventJson
{
    private final String name;

    public ContentTypeUpdatedEventJson( final ContentTypeUpdatedEvent event )
    {
        this.name = event.getName().toString();
    }

    public String getName()
    {
        return name;
    }
}
