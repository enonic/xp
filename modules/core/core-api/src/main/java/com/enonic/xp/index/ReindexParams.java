package com.enonic.xp.index;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public class ReindexParams
{
    private final boolean initialize;

    private final RepositoryId repositoryId;

    private final Branches branches;

    private final ReindexListener listener;

    private ReindexParams( Builder builder )
    {
        initialize = builder.initialize;
        repositoryId = builder.repositoryId;
        branches = Branches.from( builder.branches );
        listener = builder.listener;
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

    public ReindexListener getListener()
    {
        return this.listener;
    }


    public static final class Builder
    {
        private final Set<Branch> branches = new HashSet<>();

        private boolean initialize;

        private RepositoryId repositoryId;

        private ReindexListener listener;

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

        public Builder listener( final ReindexListener listener )
        {
            this.listener = listener;
            return this;
        }

        public ReindexParams build()
        {
            return new ReindexParams( this );
        }
    }
}


