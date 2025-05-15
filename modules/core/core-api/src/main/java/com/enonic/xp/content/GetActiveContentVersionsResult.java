package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetActiveContentVersionsResult
{
    private final ImmutableList<ActiveContentVersionEntry> activeContentVersions;

    private GetActiveContentVersionsResult( final Builder builder )
    {
        this.activeContentVersions = ImmutableList.sortedCopyOf( Comparator.
                                                                     comparing( ActiveContentVersionEntry::getContentVersion, ContentVersionDateComparator.INSTANCE ).
                                                                     thenComparing( ( ActiveContentVersionEntry activeContentVersionEntry ) -> activeContentVersionEntry.getBranch().getValue() ),
                                                                 builder.activeContentVersions );
    }

    public List<ActiveContentVersionEntry> getActiveContentVersions()
    {
        return activeContentVersions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<ActiveContentVersionEntry> activeContentVersions = new ArrayList<>();

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
