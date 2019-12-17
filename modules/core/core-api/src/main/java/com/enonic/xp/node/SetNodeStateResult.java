package com.enonic.xp.node;

import java.util.HashSet;
import java.util.Set;

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
        private final Set<Node> updatedNodes = new HashSet<>();

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
