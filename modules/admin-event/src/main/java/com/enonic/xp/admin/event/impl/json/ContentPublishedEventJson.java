package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.content.ContentPublishedEvent;

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
