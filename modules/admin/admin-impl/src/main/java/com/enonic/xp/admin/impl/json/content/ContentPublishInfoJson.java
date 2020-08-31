package com.enonic.xp.admin.impl.json.content;

import java.time.Instant;
import java.util.Objects;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ContentPublishInfoJson that = (ContentPublishInfoJson) o;
        return Objects.equals( from, that.from ) && Objects.equals( to, that.to ) && Objects.equals( first, that.first );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, to, first );
    }

}
