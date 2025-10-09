package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;

public class StoreBranchMetadataParams
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final InternalContext context;

    private StoreBranchMetadataParams( final Builder builder )
    {
        node = builder.node;
        nodeVersionId = builder.nodeVersionId;
        nodeVersionKey = builder.nodeVersionKey;
        context = builder.context;
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

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public InternalContext getContext()
    {
        return context;
    }


    public static final class Builder
    {
        private Node node;

        private NodeVersionId nodeVersionId;

        private NodeVersionKey nodeVersionKey;

        private InternalContext context;

        private Builder()
        {
        }

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeVersionKey( final NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
            return this;
        }

        public Builder context( final InternalContext context )
        {
            this.context = context;
            return this;
        }

        public StoreBranchMetadataParams build()
        {
            return new StoreBranchMetadataParams( this );
        }
    }
}
