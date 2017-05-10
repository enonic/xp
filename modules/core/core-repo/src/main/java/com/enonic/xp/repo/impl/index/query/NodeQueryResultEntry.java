package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repository.RepositoryId;

public class NodeQueryResultEntry
{
    private final float score;

    private final NodeId id;

    private final ReturnValues returnValues;

    private final RepositoryId repositoryId;

    private final Branch branch;

    private NodeQueryResultEntry( final Builder builder )
    {
        score = builder.score;
        id = builder.id;
        returnValues = builder.returnValues;
        repositoryId = builder.repositoryId;
        branch = builder.branch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public float getScore()
    {
        return score;
    }

    public NodeId getId()
    {
        return id;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public ReturnValues getReturnValues()
    {
        return returnValues;
    }

    public static final class Builder
    {
        private float score;

        private NodeId id;

        private ReturnValues returnValues;

        private RepositoryId repositoryId;

        private Branch branch;

        private Builder()
        {
        }

        public Builder score( final float val )
        {
            score = val;
            return this;
        }

        public Builder id( final NodeId val )
        {
            id = val;
            return this;
        }

        public Builder returnValues( final ReturnValues val )
        {
            returnValues = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder branch( final Branch val )
        {
            branch = val;
            return this;
        }

        public NodeQueryResultEntry build()
        {
            return new NodeQueryResultEntry( this );
        }
    }
}
