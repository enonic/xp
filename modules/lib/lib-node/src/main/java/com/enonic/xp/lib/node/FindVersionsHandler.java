package com.enonic.xp.lib.node;

import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeVersionQueryResult;

public class FindVersionsHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final int from;

    private final int size;

    private FindVersionsHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        from = builder.from;
        size = builder.size;
    }

    @Override
    public Object execute()
    {
        final NodeVersionQueryResult result = nodeService.findVersions( makeGetNodeVersionsParams() );

        return new NodeVersionsResultMapper( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private GetNodeVersionsParams makeGetNodeVersionsParams()
    {
        return GetNodeVersionsParams.create().nodeId( getNodeId( nodeKey ) ).from( from ).size( size ).build();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private int from = 0;

        private int size = 10;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public FindVersionsHandler build()
        {
            return new FindVersionsHandler( this );
        }
    }
}
