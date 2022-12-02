package com.enonic.xp.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class ContentPublishInfo
{
    private final Instant from;

    private final Instant to;

    private final Instant first;

    private ContentPublishInfo( final Builder builder )
    {
        from = builder.from != null ? builder.from.truncatedTo( ChronoUnit.MILLIS ) : null;
        to = builder.to != null ? builder.to.truncatedTo( ChronoUnit.MILLIS ) : null;
        first = builder.first != null ? builder.first.truncatedTo( ChronoUnit.MILLIS ) : null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }

    public Instant getFirst()
    {
        return first;
    }

    public static final class Builder
    {
        private Instant from;

        private Instant to;

        private Instant first;

        private Builder()
        {
        }

        public Builder from( final Instant val )
        {
            from = val;
            return this;
        }

        public Builder to( final Instant val )
        {
            to = val;
            return this;
        }

        public Builder first( final Instant val )
        {
            first = val;
            return this;
        }

        public ContentPublishInfo build()
        {
            return new ContentPublishInfo( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentPublishInfo ) )
        {
            return false;
        }
        final ContentPublishInfo that = (ContentPublishInfo) o;
        return Objects.equals( from, that.from ) && Objects.equals( to, that.to ) && Objects.equals( first, that.first );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, to, first );
    }
}
