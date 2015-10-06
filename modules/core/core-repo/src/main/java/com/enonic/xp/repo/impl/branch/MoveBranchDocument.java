package com.enonic.xp.repo.impl.branch;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

public class MoveBranchDocument
{
    private final Node node;

    private final NodePath previousPath;

    private final NodeVersionId nodeVersionId;

    private MoveBranchDocument( Builder builder )
    {
        node = builder.node;
        previousPath = builder.previousPath;
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


    public NodePath getPreviousPath()
    {
        return previousPath;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static final class Builder
    {
        private Node node;

        private NodePath previousPath;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder previousPath( NodePath previousPath )
        {
            this.previousPath = previousPath;
            return this;
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public MoveBranchDocument build()
        {
            return new MoveBranchDocument( this );
        }
    }
}
