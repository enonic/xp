package com.enonic.xp.dump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class RepoDumpResult
    implements Iterable<BranchDumpResult>
{
    private final List<BranchDumpResult> branchResults;

    private final RepositoryId repositoryId;

    private final Long versions;

    private final List<DumpError> versionsErrors;

    private RepoDumpResult( final Builder builder )
    {
        this.branchResults = builder.branchResults;
        this.repositoryId = builder.repositoryId;
        this.versions = builder.versions;
        this.versionsErrors = builder.versionsErrors;
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

    public List<DumpError> getVersionsErrors()
    {
        return versionsErrors;
    }

    public BranchDumpResult get( final Branch branch )
    {
        final Optional<BranchDumpResult> branchDumpEntry =
            this.branchResults.stream().filter( ( entry ) -> entry.getBranch().equals( branch ) ).findFirst();

        return branchDumpEntry.orElse( null );
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
        private List<BranchDumpResult> branchResults = new ArrayList<>();

        private List<DumpError> versionsErrors = new ArrayList<>();

        private RepositoryId repositoryId;

        private Long versions = 0L;

        private Builder( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
        }

        private Builder( final RepoDumpResult source )
        {
            this( source.repositoryId );
            this.branchResults = new ArrayList<>( source.branchResults );
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

        public Builder versions( final Long versions )
        {
            this.versions = versions;
            return this;
        }

        public Builder error( final DumpError error )
        {
            this.versionsErrors.add( error );
            return this;
        }

        public RepoDumpResult build()
        {
            return new RepoDumpResult( this );
        }
    }
}
