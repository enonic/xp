package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.node.Node;

public class MoveNodeParams
{
    private final Node node;

    private final boolean updateMetadataOnly;

    private MoveNodeParams( Builder builder )
    {
        node = builder.node;
        updateMetadataOnly = builder.updateMetadataOnly;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }


    public boolean isUpdateMetadataOnly()
    {
        return updateMetadataOnly;
    }

    public static final class Builder
    {
        private Node node;

        private boolean updateMetadataOnly = false;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder updateMetadataOnly( boolean updateMetadataOnly )
        {
            this.updateMetadataOnly = updateMetadataOnly;
            return this;
        }

        public MoveNodeParams build()
        {
            return new MoveNodeParams( this );
        }
    }
}
