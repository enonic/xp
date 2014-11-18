package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.content.ContentCreatedEvent;

public final class ContentCreatedEventJson
    implements EventJson
{
    private final String contentId;

    public ContentCreatedEventJson( final ContentCreatedEvent event )
    {
        this.contentId = event.getContentId().toString();
    }

    public String getContentId()
    {
        return contentId;
    }
}
