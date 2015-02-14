package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.core.content.ContentCreatedEvent;

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
