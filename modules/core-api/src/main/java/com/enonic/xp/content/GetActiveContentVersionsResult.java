package com.enonic.xp.content;

import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class GetActiveContentVersionsResult
{
    private final ImmutableSortedSet<ActiveContentVersionEntry> activeContentVersions;

    private GetActiveContentVersionsResult( final Builder builder )
    {
        this.activeContentVersions = ImmutableSortedSet.copyOf( builder.activeContentVersions );
    }

    public ImmutableSortedSet<ActiveContentVersionEntry> getActiveContentVersions()
    {
        return activeContentVersions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final SortedSet<ActiveContentVersionEntry> activeContentVersions = Sets.newTreeSet();

        private Builder()
        {
        }

        public Builder add( final ActiveContentVersionEntry activeContentVersion )
        {
            if ( activeContentVersion != null && activeContentVersion.getContentVersion() != null )
            {
                this.activeContentVersions.add( activeContentVersion );
            }

            return this;
        }

        public GetActiveContentVersionsResult build()
        {
            return new GetActiveContentVersionsResult( this );
        }
    }
}
