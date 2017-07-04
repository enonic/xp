package com.enonic.xp.impl.server.rest.model;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.RepoDumpResult;

public class RepoDumpResultJson
{
    private final List<BranchDumpResultJson> branches;

    private final String repository;

    private final Long numberOfVersions;

    private RepoDumpResultJson( final Builder builder )
    {
        this.branches = builder.branches;
        this.repository = builder.repository;
        this.numberOfVersions = builder.numberOfVersions;
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        final Builder builder = RepoDumpResultJson.create().
            repository( repoDumpResult.getRepositoryId().toString() ).
            numberOfVersions( repoDumpResult.getNumberOfVersions() );

        for ( final BranchDumpResult result : repoDumpResult )
        {
            builder.add( BranchDumpResultJson.from( result ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public List<BranchDumpResultJson> getBranches()
    {
        return branches;
    }

    @SuppressWarnings("unused")
    public String getRepositoryId()
    {
        return repository;
    }

    @SuppressWarnings("unused")
    public Long getNumberOfVersions()
    {
        return numberOfVersions;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<BranchDumpResultJson> branches = Lists.newArrayList();

        private String repository;

        private Long numberOfVersions = 0L;

        private Builder()
        {
        }

        public Builder add( final BranchDumpResultJson val )
        {
            branches.add( val );
            return this;
        }

        public Builder repository( final String val )
        {
            repository = val;
            return this;
        }

        public Builder numberOfVersions( final Long numberOfVersions )
        {
            this.numberOfVersions = numberOfVersions;
            return this;
        }

        public RepoDumpResultJson build()
        {
            return new RepoDumpResultJson( this );
        }
    }
}
