package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;

import com.enonic.xp.content.ContentVersionPublishInfo;

public class ContentVersionPublishInfoJson
{
    private final String publisher;

    private final Instant timestamp;

    private final String message;

    public ContentVersionPublishInfoJson( final ContentVersionPublishInfo publishInfo )
    {
        this.publisher = publishInfo.getPublisher().toString();
        this.timestamp = publishInfo.getTimestamp();
        this.message = publishInfo.getMessage();
    }

    public String getPublisher()
    {
        return publisher;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getMessage()
    {
        return message;
    }
}
