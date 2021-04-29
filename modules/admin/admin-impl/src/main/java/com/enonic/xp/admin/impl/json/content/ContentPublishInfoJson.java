package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.content.ContentPublishInfo;

public class ContentPublishInfoJson
{
    private final Instant from;

    private final Instant to;

    private final Instant first;

    public ContentPublishInfoJson( final ContentPublishInfo publishInfo )
    {
        this.from = publishInfo.getFrom();
        this.to = publishInfo.getTo();
        this.first = publishInfo.getFirst();
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

    @SuppressWarnings("unused")
    public Instant getFirst()
    {
        return first;
    }
}
