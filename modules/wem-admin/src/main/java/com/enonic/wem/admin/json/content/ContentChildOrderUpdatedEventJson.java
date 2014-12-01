package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.content.ContentChildOrderUpdatedEvent;

public final class ContentChildOrderUpdatedEventJson
    implements EventJson
{
    private final String contentId;

    public ContentChildOrderUpdatedEventJson( final ContentChildOrderUpdatedEvent event )
    {
        this.contentId = event.getContentId().toString();
    }

    public String getContentId()
    {
        return contentId;
    }
}
