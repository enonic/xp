package com.enonic.xp.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class MultiRepoNodeHit
    extends NodeHit
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private MultiRepoNodeHit( final Builder builder )
    {
        super( builder.nodeId, builder.score );
        branch = builder.branch;
        repositoryId = builder.repositoryId;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branch branch;

        private RepositoryId repositoryId;

        private NodeId nodeId;

        private float score;

        private Builder()
        {
        }

        public Builder branch( final Branch val )
        {
            branch = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder score( final float score )
        {
            this.score = score;
            return this;
        }

        public MultiRepoNodeHit build()
        {
            return new MultiRepoNodeHit( this );
        }
    }
}
