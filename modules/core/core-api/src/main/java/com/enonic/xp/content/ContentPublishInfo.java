package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

public class ContentPublishInfo
{
    private Instant from;

    private Instant to;

    private Instant first;

    private ContentPublishInfo( final Builder builder )
    {
        from = builder.from;
        to = builder.to;
        first = builder.first;
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentPublishInfo that = (ContentPublishInfo) o;
        return Objects.equals( from, that.from ) & Objects.equals( to, that.to ) & Objects.equals( first, that.first );

    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, to, first );
    }
}
