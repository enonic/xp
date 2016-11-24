package com.enonic.xp.content;

import java.time.Instant;

public class ContentPublishInfo
{
    private Instant from;

    private ContentPublishInfo( final Builder builder )
    {
        from = builder.from;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Instant getFrom()
    {
        return from;
    }

    public static final class Builder
    {
        private Instant from;

        private Builder()
        {
        }

        public Builder from( final Instant val )
        {
            from = val;
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

        return from != null ? from.equals( that.from ) : that.from == null;

    }

    @Override
    public int hashCode()
    {
        return from != null ? from.hashCode() : 0;
    }
}
