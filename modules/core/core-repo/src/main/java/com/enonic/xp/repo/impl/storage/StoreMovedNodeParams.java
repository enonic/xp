package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;

public class StoreMovedNodeParams
{
    private final Node node;

    private final boolean newVersion;

    private StoreMovedNodeParams( Builder builder )
    {
        node = builder.node;
        newVersion = builder.newVersion;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }

    public boolean isNewVersion()
    {
        return newVersion;
    }

    public static final class Builder
    {
        private Node node;

        private boolean newVersion = true;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder overrideVersion()
        {
            this.newVersion = false;
            return this;
        }

        public StoreMovedNodeParams build()
        {
            return new StoreMovedNodeParams( this );
        }
    }
}
