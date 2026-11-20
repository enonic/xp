package com.enonic.xp.content;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.core.internal.Millis;

/**
 * Publishing information of a content.
 * <p>
 * Read in the draft branch, it tells how the content compares to the master branch:
 * <ul>
 * <li>{@code time} is set: the current version of the content is the version published to the master branch.</li>
 * <li>{@code time} is not set: the current version has not been published. This covers content that has never been published, and
 * content that has been edited, moved or unpublished since it was last published.</li>
 * <li>{@code first} is not set: the content has never been published.</li>
 * <li>{@code from} is not set: the content is not on the master branch.</li>
 * </ul>
 * A published content is scheduled before {@code from}, online between {@code from} and {@code to}, and expired after {@code to}.
 *
 * @param from  start of the publish window, when the content becomes visible on the master branch, or {@code null} if the content is
 *              not published
 * @param to    end of the publish window, or {@code null} if the content stays online
 * @param first when the content was first published, or {@code null} if it has never been published
 * @param time  when the current version of the content was published, or {@code null} if this version has not been published
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
         * Sets the start of the publish window.
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
         * Sets the end of the publish window.
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
         * Sets when the current version of the content was published.
         *
         * @param val the time, or {@code null} for a version that has not been published
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