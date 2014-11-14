package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.content.ContentUpdatedEvent;

public final class ContentUpdatedEventJson
    implements EventJson
{
    private final String contentId;

    public ContentUpdatedEventJson( final ContentUpdatedEvent event )
    {
        this.contentId = event.getContentId().toString();
    }

    public String getContentId()
    {
        return contentId;
    }
}
