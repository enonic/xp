package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.content.ContentPublishInfo;

public class ContentPublishInfoJson
{
    private final Instant from;

    public ContentPublishInfoJson( final ContentPublishInfo publishInfo )
    {
        this.from = publishInfo.getFrom();
    }

    @SuppressWarnings("unused")
    public Instant getFrom()
    {
        return from;
    }
}
