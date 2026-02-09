package com.enonic.xp.node;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class GetNodeVersionsResult
{
    private final NodeVersions nodeVersions;

    private final long totalHits;

    @Nullable
    private final String cursor;

    private GetNodeVersionsResult( Builder builder )
    {
        nodeVersions = Objects.requireNonNull( builder.nodeVersions );
        totalHits = builder.totalHits;
        cursor = builder.cursor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Retrieves the collection node versions.
     *
     * @return the {@link  NodeVersions} instance containing version details
     */
    public NodeVersions getNodeVersions()
    {
        return nodeVersions;
    }

    /**
     * Returns the total number of hits matching the query criteria.
     * The correct number is returned only for the request without a cursor.
     *
     * @return the total hits as a long value
     */
    public long getTotalHits()
    {
        return totalHits;
    }

    /**
     * Retrieves the cursor string associated with the current result set.
     * The cursor can be used for pagination or retrieving the next set of results.
     *
     * @return the cursor as a string, or {@code null} no more results are available
     */
    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        private @Nullable NodeVersions nodeVersions;

        private long totalHits;

        private @Nullable String cursor;

        private Builder()
        {
        }

        public Builder entityVersions( final NodeVersions nodeVersions )
        {
            this.nodeVersions = nodeVersions;
            return this;
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        public GetNodeVersionsResult build()
        {
            return new GetNodeVersionsResult( this );
        }
    }
}
