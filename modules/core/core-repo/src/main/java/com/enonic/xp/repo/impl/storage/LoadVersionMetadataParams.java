package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeVersionId;

public class LoadVersionMetadataParams
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private final NodeVersionKey nodeVersionKey;

    private final NodeCommitId nodeCommitId;

    private final InternalContext context;

    private LoadVersionMetadataParams( final Builder builder )
    {
        node = builder.node;
        nodeVersionId = builder.nodeVersionId;
        nodeVersionKey = builder.nodeVersionKey;
        nodeCommitId = builder.nodeCommitId;
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

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
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

        private NodeCommitId nodeCommitId;

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

        public Builder nodeCommitId( final NodeCommitId nodeCommitId )
        {
            this.nodeCommitId = nodeCommitId;
            return this;
        }

        public Builder context( final InternalContext context )
        {
            this.context = context;
            return this;
        }

        public LoadVersionMetadataParams build()
        {
            return new LoadVersionMetadataParams( this );
        }
    }
}
