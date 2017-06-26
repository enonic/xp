package com.enonic.xp.dump;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.repository.RepositoryId;

public class SystemDumpResult
    implements Iterable<RepoDumpResult>
{
    private final List<RepoDumpResult> repoDumpResults;

    private SystemDumpResult( final Builder builder )
    {
        repoDumpResults = builder.repoDumpResults;
    }

    public RepoDumpResult get( final RepositoryId repositoryId )
    {
        for ( final RepoDumpResult repoDumpResult : this.repoDumpResults )
        {
            if ( repoDumpResult.getRepositoryId().equals( repositoryId ) )
            {
                return repoDumpResult;
            }
        }

        return null;
    }

    @Override
    public Iterator<RepoDumpResult> iterator()
    {
        return repoDumpResults.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<RepoDumpResult> repoDumpResults = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final RepoDumpResult val )
        {
            repoDumpResults.add( val );
            return this;
        }

        public SystemDumpResult build()
        {
            return new SystemDumpResult( this );
        }
    }


}
