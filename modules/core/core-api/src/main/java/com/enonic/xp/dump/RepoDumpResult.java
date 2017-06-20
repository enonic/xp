package com.enonic.xp.dump;

import java.time.Duration;
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

    private final Duration duration;

    private RepoDumpResult( final Builder builder )
    {
        this.branchResults = builder.branchResults;
        this.repositoryId = builder.repositoryId;
        this.duration = Duration.ofMillis( builder.endTime - builder.startTime );
    }

    public List<BranchDumpResult> getBranchResults()
    {
        return branchResults;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public String getDuration()
    {
        return duration.toString();
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

    public static final class Builder
    {
        private final List<BranchDumpResult> branchResults = Lists.newArrayList();

        private final RepositoryId repositoryId;

        private final Long startTime;

        private Long endTime;

        private Builder( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            this.startTime = System.currentTimeMillis();
        }

        public Builder add( final BranchDumpResult val )
        {
            branchResults.add( val );
            return this;
        }

        public RepoDumpResult build()
        {
            this.endTime = System.currentTimeMillis();
            return new RepoDumpResult( this );
        }
    }


    public BranchDumpResult get( final Branch branch )
    {
        final Optional<BranchDumpResult> branchDumpEntry =
            this.branchResults.stream().filter( ( entry ) -> entry.getBranch().equals( branch ) ).findFirst();

        return branchDumpEntry.isPresent() ? branchDumpEntry.get() : null;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "DumpResult{" );
        this.branchResults.forEach( ( entry ) -> builder.append( entry.toString() ).append( ", " ) );
        builder.append( ", repositoryId=" + repositoryId );
        builder.append( '}' );

        return builder.toString();
    }
}
