package com.enonic.xp.content;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ContentPublishInfo(@Nullable Instant from, @Nullable Instant to, @Nullable Instant first, @Nullable Instant published)
{
    public ContentPublishInfo
    {
        from = from != null ? from.truncatedTo( ChronoUnit.MILLIS ) : null;
        to = to != null ? to.truncatedTo( ChronoUnit.MILLIS ) : null;
        first = first != null ? first.truncatedTo( ChronoUnit.MILLIS ) : null;
        published = published != null ? published.truncatedTo( ChronoUnit.MILLIS ) : null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ContentPublishInfo info )
    {
        return new Builder().from( info.from() ).to( info.to() ).first( info.first() ).published( info.published() );
    }

    public static final class Builder
    {
        @Nullable
        private Instant from;

        @Nullable
        private Instant to;

        @Nullable
        private Instant first;

        @Nullable
        private Instant published;

        private Builder()
        {
        }

        public Builder from( final @Nullable Instant val )
        {
            from = val;
            return this;
        }

        public Builder to( final @Nullable Instant val )
        {
            to = val;
            return this;
        }

        public Builder first( final @Nullable Instant val )
        {
            first = val;
            return this;
        }

        public Builder published( final @Nullable Instant val )
        {
            published = val;
            return this;
        }

        public ContentPublishInfo build()
        {
            return new ContentPublishInfo( from, to, first, published );
        }
    }
}