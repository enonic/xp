package com.enonic.xp.content;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.core.internal.Millis;

/**
 * Publishing information of a content.
 * <p>
 * Read in the draft branch, it tells the publish status of the content:
 * <ul>
 * <li>No {@code from}: the content is not published, either never or since it was unpublished.</li>
 * <li>Both {@code from} and {@code time}: the content in the draft branch is the content published to the master branch. It is online
 * between {@code from} and {@code to}, scheduled before {@code from} and expired after {@code to}.</li>
 * <li>{@code from} without {@code time}: the content is published and changed since. Updating, moving, renaming and sorting a content
 * removes {@code time} in the draft branch. {@link ContentService#patch(PatchContentParams)} and node level changes keep it.</li>
 * </ul>
 *
 * @param from  when the content goes online, or {@code null} if the content is not published
 * @param to    when the content goes offline, or {@code null} if it stays online
 * @param first when the content was first published, or {@code null} if it has never been published
 * @param time  when the content was last published, or {@code null} if it is not published or changed since publishing
 */
@NullMarked
public record ContentPublishInfo(@Nullable Instant from, @Nullable Instant to, @Nullable Instant first, @Nullable Instant time)
{
    public ContentPublishInfo
    {
        from = Millis.from( from );
        to = Millis.from( to );
        first = Millis.from( first );
        time = Millis.from( time );
    }

    /**
     * Creates a builder with no values set.
     *
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Creates a builder holding the values of the given publishing information.
     *
     * @param info publishing information to copy
     * @return a new builder
     */
    public static Builder create( final ContentPublishInfo info )
    {
        return new Builder().from( info.from() ).to( info.to() ).first( info.first() ).time( info.time() );
    }

    /**
     * Builder of {@link ContentPublishInfo}.
     */
    public static final class Builder
    {
        @Nullable
        private Instant from;

        @Nullable
        private Instant to;

        @Nullable
        private Instant first;

        @Nullable
        private Instant time;

        private Builder()
        {
        }

        /**
         * Sets when the content goes online.
         *
         * @param val the time, or {@code null} for a content that is not published
         * @return this builder
         */
        public Builder from( final @Nullable Instant val )
        {
            from = val;
            return this;
        }

        /**
         * Sets when the content goes offline.
         *
         * @param val the time, or {@code null} for a content that stays online
         * @return this builder
         */
        public Builder to( final @Nullable Instant val )
        {
            to = val;
            return this;
        }

        /**
         * Sets when the content was first published.
         *
         * @param val the time, or {@code null} for a content that has never been published
         * @return this builder
         */
        public Builder first( final @Nullable Instant val )
        {
            first = val;
            return this;
        }

        /**
         * Sets when the content was last published.
         *
         * @param val the time, or {@code null} for a content that is not published or changed since publishing
         * @return this builder
         */
        public Builder time( final @Nullable Instant val )
        {
            time = val;
            return this;
        }

        /**
         * Builds the publishing information.
         *
         * @return the publishing information
         */
        public ContentPublishInfo build()
        {
            return new ContentPublishInfo( from, to, first, time );
        }
    }
}