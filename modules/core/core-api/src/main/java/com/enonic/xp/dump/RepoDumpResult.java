package com.enonic.xp.dump;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class RepoDumpResult
    implements Iterable<BranchDumpResult>
{
    private final List<BranchDumpResult> branchResults;

    private final RepositoryId repositoryId;

    private final Long versions;

    private RepoDumpResult( final Builder builder )
    {
        this.branchResults = builder.branchResults;
        this.repositoryId = builder.repositoryId;
        this.versions = builder.versions;
    }

    public List<BranchDumpResult> getBranchResults()
    {
        return branchResults;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Long getVersions()
    {
        return versions;
    }

    public BranchDumpResult get( final Branch branch )
    {
        final Optional<BranchDumpResult> branchDumpEntry =
            this.branchResults.stream().filter( ( entry ) -> entry.getBranch().equals( branch ) ).findFirst();

        return branchDumpEntry.isPresent() ? branchDumpEntry.get() : null;
    }

    @Override
    public Iterator<BranchDumpResult> iterator()
    {
        return this.branchResults.iterator();
    }

    public static Builder create( final RepositoryId repositoryId )
    {
        return new Builder( repositoryId );
    }

    public static Builder create( final RepoDumpResult source )
    {
        return new Builder( source );
    }

    public static final class Builder
    {
        private List<BranchDumpResult> branchResults = Lists.newArrayList();

        private RepositoryId repositoryId;

        private Long versions = 0L;

        private Builder( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
        }

        private Builder( final RepoDumpResult source )
        {
            this( source.repositoryId );
            this.branchResults = Lists.newArrayList( source.branchResults );
            this.versions = source.versions;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder add( final BranchDumpResult val )
        {
            branchResults.add( val );
            return this;
        }

        public Builder addedVersion()
        {
            this.versions++;
            return this;
        }

        public Builder versions( final Long versions)
        {
            this.versions = versions;
            return this;
        }

        public RepoDumpResult build()
        {
            return new RepoDumpResult( this );
        }
    }
}
