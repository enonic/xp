package com.enonic.xp.index;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

@Beta
public class ReindexParams
{
    private final boolean initialize;

    private final RepositoryId repositoryId;

    private final Branches branches;

    private final ProgressReporter progressReporter;

    private ReindexParams( Builder builder )
    {
        initialize = builder.initialize;
        repositoryId = builder.repositoryId;
        branches = Branches.from( builder.branches );
        progressReporter = builder.progressReporter;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean isInitialize()
    {
        return initialize;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public ProgressReporter getProgressReporter()
    {
        return this.progressReporter;
    }


    public static final class Builder
    {
        private final Set<Branch> branches = new HashSet<>();

        private boolean initialize;

        private RepositoryId repositoryId;

        private ProgressReporter progressReporter;

        private Builder()
        {
        }

        public Builder initialize( final boolean initialize )
        {
            this.initialize = initialize;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder addBranch( final Branch branch )
        {
            this.branches.add( branch );
            return this;
        }

        public Builder setBranches( final Branches branches )
        {
            this.branches.addAll( branches.getSet() );
            return this;
        }

        public Builder progressReporter( final ProgressReporter progressReporter )
        {
            this.progressReporter = progressReporter;
            return this;
        }

        public ReindexParams build()
        {
            return new ReindexParams( this );
        }
    }
}


