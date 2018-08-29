package com.enonic.xp.lib.node;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.SetNodeChildOrderParams;

public class SetChildOrderHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final ChildOrder childOrder;

    private SetChildOrderHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        childOrder = builder.childOrder;
    }

    @Override
    public Object execute()
    {
        final Node node = nodeService.setChildOrder( makeSetNodeChildOrderParams() );
        return new NodeMapper( node );
    }

    private SetNodeChildOrderParams makeSetNodeChildOrderParams()
    {
        return SetNodeChildOrderParams.create().nodeId( getNodeId( nodeKey ) ).childOrder( childOrder ).build();
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

        public SetChildOrderHandler build()
        {
            return new SetChildOrderHandler( this );
        }
    }
}
