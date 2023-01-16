package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodePath;

public class StoreNodeParams
{
    private final Node node;

    private final NodeCommitId nodeCommitId;

    private final boolean newVersion;

    private final NodePath movedFrom;

    private StoreNodeParams( final Builder builder )
    {
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
        newVersion = builder.newVersion;
        movedFrom = builder.movedFrom;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public boolean isNewVersion()
    {
        return newVersion;
    }

    public NodePath movedFrom()
    {
        return movedFrom;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private NodeCommitId nodeCommitId;

        private boolean newVersion = true;

        private NodePath movedFrom;

        private Builder()
        {
        }

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId nodeCommitId )
        {
            this.nodeCommitId = nodeCommitId;
            return this;
        }

        public Builder overrideVersion()
        {
            this.newVersion = false;
            return this;
        }

        public Builder movedFrom( final NodePath movedFrom)
        {
            this.movedFrom = movedFrom;
            return this;
        }

        public StoreNodeParams build()
        {
            return new StoreNodeParams( this );
        }
    }
}
