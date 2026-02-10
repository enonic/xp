package com.enonic.xp.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

public final class ContentPublishInfo
{
    private final Instant from;

    private final Instant to;

    private final Instant first;

    private final Instant published;

    private ContentPublishInfo( final Builder builder )
    {
        from = builder.from != null ? builder.from.truncatedTo( ChronoUnit.MILLIS ) : null;
        to = builder.to != null ? builder.to.truncatedTo( ChronoUnit.MILLIS ) : null;
        first = builder.first != null ? builder.first.truncatedTo( ChronoUnit.MILLIS ) : null;
        published = builder.published != null ? builder.published.truncatedTo( ChronoUnit.MILLIS ) : null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final @NonNull ContentPublishInfo info )
    {
        return new Builder().from( info.getFrom() ).to( info.getTo() ).first( info.getFirst() ).published( info.getPublished() );
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

    public Instant getPublished()
    {
        return published;
    }

    public static final class Builder
    {
        private Instant from;

        private Instant to;

        private Instant first;

        private Instant published;

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

        public Builder published( final Instant val )
        {
            published = val;
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
        return this == o ||
            o instanceof final ContentPublishInfo that && Objects.equals( from, that.from ) && Objects.equals( to, that.to ) &&
                Objects.equals( first, that.first ) && Objects.equals( published, that.published );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, to, first );
    }
}
