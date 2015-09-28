package com.enonic.xp.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class SetNodeStateResult
{
    private final Nodes updatedNodes;

    private SetNodeStateResult( Builder builder )
    {
        updatedNodes = Nodes.from( builder.updatedNodes );
    }

    public Nodes getUpdatedNodes()
    {
        return updatedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final Set<Node> updatedNodes = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder addUpdatedNode( final Node updatedNodes )
        {
            this.updatedNodes.add( updatedNodes );
            return this;
        }

        public SetNodeStateResult build()
        {
            return new SetNodeStateResult( this );
        }
    }
}
