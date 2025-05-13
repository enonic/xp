package com.enonic.xp.dump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.enonic.xp.repository.RepositoryId;

public final class RepoLoadResult
    implements Iterable<BranchLoadResult>
{
    private final List<BranchLoadResult> branchResults;

    private final VersionsLoadResult versionsLoadResult;

    private final CommitsLoadResult commitsLoadResult;

    private final RepositoryId repositoryId;

    private RepoLoadResult( final Builder builder )
    {
        this.branchResults = builder.branchResults;
        this.repositoryId = builder.repositoryId;
        this.versionsLoadResult = builder.versionsLoadResult;
        this.commitsLoadResult = builder.commitsLoadResult;
    }

    @Override
    public Iterator<BranchLoadResult> iterator()
    {
        return this.branchResults.iterator();
    }

    @SuppressWarnings("unused")
    public List<BranchLoadResult> getBranchResults()
    {
        return branchResults;
    }

    @SuppressWarnings("unused")
    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public VersionsLoadResult getVersionsLoadResult()
    {
        return versionsLoadResult;
    }

    public CommitsLoadResult getCommitsLoadResult()
    {
        return commitsLoadResult;
    }

    public static Builder create( final RepositoryId repositoryId )
    {
        return new Builder( repositoryId );
    }

    public static final class Builder
    {
        private final List<BranchLoadResult> branchResults = new ArrayList<>();

        private final RepositoryId repositoryId;

        private VersionsLoadResult versionsLoadResult = VersionsLoadResult.create().build();

        private CommitsLoadResult commitsLoadResult;


        private Builder( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
        }

        public Builder add( final BranchLoadResult val )
        {
            branchResults.add( val );
            return this;
        }

        public Builder versions( final VersionsLoadResult val )
        {
            versionsLoadResult = val;
            return this;
        }

        public Builder commits( final CommitsLoadResult val )
        {
            commitsLoadResult = val;
            return this;
        }

        public RepoLoadResult build()
        {
            return new RepoLoadResult( this );
        }
    }
}
