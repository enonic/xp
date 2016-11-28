package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.content.ContentPublishInfo;

public class ContentPublishInfoJson
{
    private final Instant from;

    private final Instant to;

    public ContentPublishInfoJson( final ContentPublishInfo publishInfo )
    {
        this.from = publishInfo.getFrom();
        this.to = publishInfo.getTo();
    }

    @SuppressWarnings("unused")
    public Instant getFrom()
    {
        return from;
    }

    @SuppressWarnings("unused")
    public Instant getTo()
    {
        return to;
    }
}
