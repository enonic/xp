package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class SetActiveVersionResult
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    public SetActiveVersionResult( Builder builder )
    {
        node = builder.node;
        nodeVersionId = builder.nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static final class Builder
    {
        private Node node;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder versionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }


        public SetActiveVersionResult build()
        {
            return new SetActiveVersionResult( this );
        }
    }
}
