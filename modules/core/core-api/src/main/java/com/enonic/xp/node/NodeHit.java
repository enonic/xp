package com.enonic.xp.node;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.query.QueryExplanation;

public class NodeHit
{
    private final NodeId nodeId;

    private final float score;

    private final QueryExplanation explanation;

    private final HighlightedProperties highlight;

    private NodeHit( final Builder builder )
    {
        nodeId = builder.nodeId;
        score = builder.score;
        explanation = builder.explanation;
        highlight = builder.highlight.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
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

    public static final class Builder
    {
        private NodeId nodeId;

        private float score;

        private QueryExplanation explanation;

        private HighlightedProperties.Builder highlight = HighlightedProperties.create();

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
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


        public NodeHit build()
        {
            return new NodeHit( this );
        }
    }
}
