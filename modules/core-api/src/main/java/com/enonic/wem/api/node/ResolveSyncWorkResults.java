package com.enonic.wem.api.node;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ResolveSyncWorkResults
    implements Iterable<ResolveSyncWorkResult>
{
    private final ImmutableSet<ResolveSyncWorkResult> results;

    private ResolveSyncWorkResults( final Builder builder )
    {
        this.results = ImmutableSet.copyOf( builder.results );
    }

    public ImmutableSet<ResolveSyncWorkResult> getResults()
    {
        return results;
    }

    public boolean hasNotice()
    {
        for ( final ResolveSyncWorkResult result : results )
        {
            if ( result.hasPublishOutsideSelection() )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<ResolveSyncWorkResult> iterator()
    {
        return results.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<ResolveSyncWorkResult> results = Sets.newHashSet();

        public Builder add( final ResolveSyncWorkResult result )
        {
            this.results.add( result );
            return this;
        }

        public ResolveSyncWorkResults build()
        {
            return new ResolveSyncWorkResults( this );
        }

    }

}
