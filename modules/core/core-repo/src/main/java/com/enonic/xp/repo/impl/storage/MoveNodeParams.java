package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;

public class MoveNodeParams
{
    private final Node node;

    private final boolean updateMetadataOnly;

    private final boolean setInherited;

    private MoveNodeParams( Builder builder )
    {
        node = builder.node;
        updateMetadataOnly = builder.updateMetadataOnly;
        setInherited = builder.setInherited;
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

    public boolean isSetInherited()
    {
        return setInherited;
    }

    public static final class Builder
    {
        private Node node;

        private boolean updateMetadataOnly = false;

        private boolean setInherited = false;

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

        public Builder setInherited( boolean setInherited )
        {
            this.setInherited = setInherited;
            return this;
        }

        public MoveNodeParams build()
        {
            return new MoveNodeParams( this );
        }
    }
}
