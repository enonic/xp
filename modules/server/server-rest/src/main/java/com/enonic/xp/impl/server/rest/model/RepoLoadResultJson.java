package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.RepoLoadResult;

public class RepoLoadResultJson
{
    private final String duration;

    private final List<BranchLoadResultJson> branches;

    private final String repository;

    private RepoLoadResultJson( final Builder builder )
    {
        duration = builder.duration;
        branches = builder.branches;
        this.repository = builder.repository;
    }

    public static RepoLoadResultJson from( final RepoLoadResult results )
    {
        final Builder builder = RepoLoadResultJson.create();
        builder.repository = results.getRepositoryId().toString();
        builder.duration = results.getDuration().toString();

        for ( final BranchLoadResult result : results )
        {
            builder.add( BranchLoadResultJson.from( result ) );
        }

        return builder.build();
    }

    @SuppressWarnings( "unused" )
    public String getDuration()
    {
        return duration;
    }

    @SuppressWarnings( "unused" )
    public List<BranchLoadResultJson> getBranches()
    {
        return branches;
    }

    @SuppressWarnings( "unused" )
    public String getRepository()
    {
        return repository;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String duration;

        private final List<BranchLoadResultJson> branches = Lists.newArrayList();

        private String repository;

        private Builder()
        {
        }

        public Builder duration( final String val )
        {
            duration = val;
            return this;
        }

        public Builder add( final BranchLoadResultJson val )
        {
            branches.add( val );
            return this;
        }

        public RepoLoadResultJson build()
        {
            return new RepoLoadResultJson( this );
        }
    }
}
