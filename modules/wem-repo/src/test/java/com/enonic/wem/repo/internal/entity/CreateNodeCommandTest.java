package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;

import static junit.framework.Assert.assertTrue;

public class CreateNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    public void populate_manual_order_value_and_insert_first()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "my-node" ) ).
            parent( NodePath.ROOT ).
            name( "my-node" ).
            childOrder( ChildOrder.manualOrder() ).
            build() );

        final Node c1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-1" ) ).
            parent( parentNode.path() ).
            name( "child-node-1" ).
            build() );

        final Node c2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-2" ) ).
            parent( parentNode.path() ).
            name( "child-node-2" ).
            build() );

        final Node c3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-3" ) ).
            parent( parentNode.path() ).
            name( "child-node-3" ).
            build() );
        refresh();

        assertTrue( c1.getManualOrderValue() != null && c2.getManualOrderValue() != null && c3.getManualOrderValue() != null );
        assertTrue( c1.getManualOrderValue() < c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() < c3.getManualOrderValue() );
    }


    @Test
    public void populate_manual_order_value_and_insert_last()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "my-node" ) ).
            parent( NodePath.ROOT ).
            name( "my-node" ).
            childOrder( ChildOrder.manualOrder() ).
            build() );

        final Node c1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-1" ) ).
            parent( parentNode.path() ).
            name( "child-node-1" ).
            insertManualAtBottom( true ).
            build() );

        final Node c2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-2" ) ).
            parent( parentNode.path() ).
            name( "child-node-2" ).
            insertManualAtBottom( true ).
            build() );

        final Node c3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-3" ) ).
            parent( parentNode.path() ).
            name( "child-node-3" ).
            insertManualAtBottom( true ).
            build() );
        refresh();

        assertTrue( c1.getManualOrderValue() != null && c2.getManualOrderValue() != null && c3.getManualOrderValue() != null );
        assertTrue( c1.getManualOrderValue() > c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() > c3.getManualOrderValue() );
    }


}
