package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class SetActiveVersionHandler
    extends AbstractNodeHandler
{
    private final NodeKey key;

    private final NodeVersionId versionId;

    private SetActiveVersionHandler( final Builder builder )
    {
        super( builder );
        key = builder.key;
        versionId = builder.versionId;
    }

    @Override
    public Boolean execute()
    {
        final NodeId nodeId = getNodeId( key );
        if ( nodeId == null )
        {
            return false;
        }
        return nodeService.setActiveVersion( nodeId, versionId ) != null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private NodeVersionId versionId;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder versionId( final NodeVersionId val )
        {
            versionId = val;
            return this;
        }

        public SetActiveVersionHandler build()
        {
            return new SetActiveVersionHandler( this );
        }
    }
}
