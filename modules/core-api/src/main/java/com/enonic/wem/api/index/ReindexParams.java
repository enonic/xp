package com.enonic.wem.api.index;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.branch.Branches;
import com.enonic.wem.api.repository.RepositoryId;

public class ReindexParams
{
    private final boolean initialize;

    private final RepositoryId repositoryId;

    private final Branches branches;

    private ReindexParams( Builder builder )
    {
        initialize = builder.initialize;
        repositoryId = builder.repositoryId;
        branches = Branches.from( builder.branches );
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


    public static final class Builder
    {
        private final Set<Branch> branches = Sets.newHashSet();

        private boolean initialize;

        private RepositoryId repositoryId;

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

        public ReindexParams build()
        {
            return new ReindexParams( this );
        }
    }
}


