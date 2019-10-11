package com.enonic.xp.content;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSortedSet;

@Beta
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
        private final SortedSet<ActiveContentVersionEntry> activeContentVersions = new TreeSet<ActiveContentVersionEntry>();

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
