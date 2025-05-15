package com.enonic.xp.node;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.sortvalues.SortValuesProperty;

public final class NodeHit
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final float score;

    private final QueryExplanation explanation;

    private final HighlightedProperties highlight;

    private final SortValuesProperty sort;

    private NodeHit( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        score = builder.score;
        explanation = builder.explanation;
        highlight = builder.highlight.build();
        sort = builder.sort;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public float getScore()
    {
        return score;
    }

    public QueryExplanation getExplanation()
    {
        return explanation;
    }

    public HighlightedProperties getHighlight()
    {
        return highlight;
    }

    public SortValuesProperty getSort()
    {
        return sort;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private float score;

        private QueryExplanation explanation;

        private HighlightedProperties.Builder highlight = HighlightedProperties.create();

        private SortValuesProperty sort;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder score( final float val )
        {
            score = val;
            return this;
        }

        public Builder explanation( final QueryExplanation val )
        {
            explanation = val;
            return this;
        }

        public Builder highlight( final HighlightedProperties val )
        {
            highlight = HighlightedProperties.create( val );
            return this;
        }

        public Builder sort( final SortValuesProperty sortValues )
        {
            this.sort = sortValues;
            return this;
        }

        public NodeHit build()
        {
            return new NodeHit( this );
        }
    }
}
