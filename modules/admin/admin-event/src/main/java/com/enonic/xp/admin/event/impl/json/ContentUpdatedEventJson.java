package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.content.ContentUpdatedEvent;

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
