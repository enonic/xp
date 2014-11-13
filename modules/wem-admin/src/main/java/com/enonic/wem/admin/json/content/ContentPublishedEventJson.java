package com.enonic.wem.admin.json.content;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.content.ContentPublishedEvent;

public final class ContentPublishedEventJson
    implements EventJson
{
    private final String contentId;

    public ContentPublishedEventJson( final ContentPublishedEvent event )
    {
        this.contentId = event.getContentId().toString();
    }

    public String getContentId()
    {
        return contentId;
    }
}
