package com.enonic.xp.lib.node;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.SortNodeParams;

public class SortHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final ChildOrder childOrder;

    private SortHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        childOrder = builder.childOrder;
    }

    @Override
    public Object execute()
    {
        final Node node = nodeService.sort( makeSetNodeChildOrderParams() ).getNode();
        return new NodeMapper( node );
    }

    private SortNodeParams makeSetNodeChildOrderParams()
    {
        return SortNodeParams.create().nodeId( getNodeId( nodeKey ) ).childOrder( childOrder ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private ChildOrder childOrder;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder childOrder( final ChildOrder val )
        {
            childOrder = val;
            return this;
        }

        public SortHandler build()
        {
            return new SortHandler( this );
        }
    }
}
