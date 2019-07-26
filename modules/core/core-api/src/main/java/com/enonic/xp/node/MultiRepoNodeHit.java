package com.enonic.xp.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.repository.RepositoryId;

public class MultiRepoNodeHit
{
    private final NodeId nodeId;

    private final float score;

    private final RepositoryId repositoryId;

    private final Branch branch;

    private final QueryExplanation explanation;

    private final HighlightedFields highlight;

    private MultiRepoNodeHit( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.score = builder.score;
        this.branch = builder.branch;
        this.repositoryId = builder.repositoryId;
        this.explanation = builder.explanation;
        this.highlight = builder.highlight.build();
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public QueryExplanation getExplanation()
    {
        return explanation;
    }

    public float getScore()
    {
        return score;
    }

    public HighlightedFields getHighlight()
    {
        return highlight;
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

        private QueryExplanation explanation;

        private HighlightedFields.Builder highlight = HighlightedFields.create();

        private Builder()
        {
        }

        public Builder explanation( final QueryExplanation explanation )
        {
            this.explanation = explanation;
            return this;
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

        public Builder highlight( final HighlightedFields val )
        {
            highlight = HighlightedFields.create( val );
            return this;
        }

        public MultiRepoNodeHit build()
        {
            return new MultiRepoNodeHit( this );
        }
    }
}
