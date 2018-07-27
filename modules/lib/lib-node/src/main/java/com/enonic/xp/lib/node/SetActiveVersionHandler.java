package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeVersionId;

public class SetActiveVersionHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final NodeVersionId versionId;

    private SetActiveVersionHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        versionId = builder.versionId;
    }

    @Override
    public Object execute()
    {
        return nodeService.setActiveVersion( getNodeId( nodeKey ), versionId ).toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private NodeVersionId versionId;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
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
