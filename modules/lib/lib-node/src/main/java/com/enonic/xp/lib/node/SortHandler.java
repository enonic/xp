package com.enonic.xp.lib.node;

import java.util.List;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.node.mapper.SortNodeResultMapper;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.script.ScriptValue;

public class SortHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final String childOrder;

    private final ScriptValue reorder;

    private SortHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        childOrder = builder.childOrder;
        reorder = builder.reorder;
    }

    @Override
    public SortNodeResultMapper execute()
    {
        final List<ReorderChildNodeParams> reorderParams = parseReorder();

        final SortNodeParams.Builder builder =
            SortNodeParams.create().nodeId( getNodeId( nodeKey ) ).childOrder( resolveChildOrder( reorderParams ) );
        reorderParams.forEach( builder::addManualOrder );

        return new SortNodeResultMapper( nodeService.sort( builder.build() ) );
    }

    private ChildOrder resolveChildOrder( final List<ReorderChildNodeParams> reorderParams )
    {
        if ( "manual".equals( this.childOrder ) )
        {
            return ChildOrder.orderKeyOrder();
        }
        if ( this.childOrder != null )
        {
            return ChildOrder.from( this.childOrder );
        }
        if ( !reorderParams.isEmpty() )
        {
            // reordering only makes sense under manual ordering, so flip to it
            return ChildOrder.orderKeyOrder();
        }
        throw new IllegalArgumentException( "Either 'childOrder' or 'reorder' is required" );
    }

    private List<ReorderChildNodeParams> parseReorder()
    {
        if ( this.reorder == null )
        {
            return List.of();
        }
        return this.reorder.getArray().stream().map( SortHandler::convertToReorderParams ).toList();
    }

    private static ReorderChildNodeParams convertToReorderParams( final ScriptValue entry )
    {
        final ScriptValue nodeId = entry.getMember( "nodeId" );
        if ( nodeId == null )
        {
            throw new IllegalArgumentException( "Parameter 'nodeId' is required in reorder entries" );
        }
        return ReorderChildNodeParams.create()
            .nodeId( NodeId.from( nodeId.getValue( String.class ) ) )
            .afterOrderKey( getStringMember( entry, "afterOrderKey" ) )
            .beforeOrderKey( getStringMember( entry, "beforeOrderKey" ) )
            .build();
    }

    private static String getStringMember( final ScriptValue entry, final String name )
    {
        final ScriptValue member = entry.getMember( name );
        return member != null ? member.getValue( String.class ) : null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private String childOrder;

        private ScriptValue reorder;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder childOrder( final String val )
        {
            childOrder = val;
            return this;
        }

        public Builder reorder( final ScriptValue val )
        {
            reorder = val;
            return this;
        }

        public SortHandler build()
        {
            return new SortHandler( this );
        }
    }
}
