package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.RepoLoadResult;

public class RepoLoadResultJson
{
    private final String duration;

    private final List<BranchLoadResultJson> branches;

    private final VersionsLoadResultJson versions;

    private final String repository;

    private RepoLoadResultJson( final Builder builder )
    {
        this.duration = builder.duration;
        this.branches = builder.branches;
        this.versions = builder.versions;
        this.repository = builder.repository;
    }

    public static RepoLoadResultJson from( final RepoLoadResult results )
    {
        final Builder builder = RepoLoadResultJson.create( results.getRepositoryId().toString() ).
            duration( results.getDuration().toString() ).
            versions( VersionsLoadResultJson.from( results.getVersionsLoadResult() ) );

        for ( final BranchLoadResult result : results )
        {
            builder.add( BranchLoadResultJson.from( result ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public String getDuration()
    {
        return duration;
    }

    @SuppressWarnings("unused")
    public List<BranchLoadResultJson> getBranches()
    {
        return branches;
    }

    @SuppressWarnings("unused")
    public String getRepository()
    {
        return repository;
    }

    @SuppressWarnings("unused")
    public VersionsLoadResultJson getVersions()
    {
        return versions;
    }

    private static Builder create( final String repositoryId )
    {
        return new Builder( repositoryId );
    }

    public static final class Builder
    {
        private String duration;

        private final List<BranchLoadResultJson> branches = Lists.newArrayList();

        private VersionsLoadResultJson versions;

        private final String repository;

        private Builder( final String repositoryId )
        {
            this.repository = repositoryId;
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

        public Builder versions( final VersionsLoadResultJson val )
        {
            this.versions = val;
            return this;
        }

        public RepoLoadResultJson build()
        {
            return new RepoLoadResultJson( this );
        }
    }
}
