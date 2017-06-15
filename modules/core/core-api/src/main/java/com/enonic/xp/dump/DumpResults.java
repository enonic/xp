package com.enonic.xp.dump;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.repository.RepositoryId;

public class DumpResults
    implements Iterable<DumpResult>
{
    private final List<DumpResult> dumpResults;

    private DumpResults( final Builder builder )
    {
        dumpResults = builder.dumpResults;
    }

    public DumpResult get( final RepositoryId repositoryId )
    {
        for ( final DumpResult dumpResult : this.dumpResults )
        {
            if ( dumpResult.getRepositoryId().equals( repositoryId ) )
            {
                return dumpResult;
            }
        }

        return null;
    }

    @Override
    public Iterator<DumpResult> iterator()
    {
        return dumpResults.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<DumpResult> dumpResults = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final DumpResult val )
        {
            dumpResults.add( val );
            return this;
        }

        public DumpResults build()
        {
            return new DumpResults( this );
        }
    }


}
