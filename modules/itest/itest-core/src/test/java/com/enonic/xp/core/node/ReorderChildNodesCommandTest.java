package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.ReorderChildNodesParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.node.ReorderChildNodesCommand;
import com.enonic.xp.repo.impl.node.SetNodeChildOrderCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReorderChildNodesCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void move_first()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        setChildOrder( parentNode, NodeIndexPath.NAME, OrderExpr.Direction.ASC );
        setChildOrder( parentNode, NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        ReorderChildNodesCommand.create()
            .params( ReorderChildNodesParams.create()
                         .add( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "a" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: c,a,b,d,e,f
        final Iterator<NodeId> iterator = reOrderedResult.getNodeIds().iterator();
        assertEquals( "c", iterator.next().toString() );
        assertEquals( "a", iterator.next().toString() );
        assertEquals( "b", iterator.next().toString() );
        assertEquals( "d", iterator.next().toString() );
        assertEquals( "e", iterator.next().toString() );
        assertEquals( "f", iterator.next().toString() );
    }

    @Test
    public void move_in_between()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        setChildOrder( parentNode, NodeIndexPath.NAME, OrderExpr.Direction.ASC );
        setChildOrder( parentNode, NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        ReorderChildNodesCommand.create()
            .params( ReorderChildNodesParams.create()
                         .add( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "b" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: a,c,b,d,e,f
        final Iterator<NodeId> iterator = reOrderedResult.getNodeIds().iterator();
        assertEquals( "a", iterator.next().toString() );
        assertEquals( "c", iterator.next().toString() );
        assertEquals( "b", iterator.next().toString() );
        assertEquals( "d", iterator.next().toString() );
        assertEquals( "e", iterator.next().toString() );
        assertEquals( "f", iterator.next().toString() );
    }

    @Test
    public void move_last()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        setChildOrder( parentNode, NodeIndexPath.NAME, OrderExpr.Direction.ASC );
        setChildOrder( parentNode, NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        ReorderChildNodesCommand.create()
            .params( ReorderChildNodesParams.create()
                         .add( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: a,b,d,e,f,c
        final Iterator<NodeId> iterator = reOrderedResult.getNodeIds().iterator();
        assertEquals( "a", iterator.next().toString() );
        assertEquals( "b", iterator.next().toString() );
        assertEquals( "d", iterator.next().toString() );
        assertEquals( "e", iterator.next().toString() );
        assertEquals( "f", iterator.next().toString() );
        assertEquals( "c", iterator.next().toString() );
    }

    @Test
    public void move_last_missing_order_values()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        setChildOrder( parentNode, NodeIndexPath.NAME, OrderExpr.Direction.ASC );
        setChildOrder( parentNode, NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC );

        setManualOrderValueToNull( NodeId.from( "a" ) );
        setManualOrderValueToNull( NodeId.from( "b" ) );
        setManualOrderValueToNull( NodeId.from( "c" ) );
        setManualOrderValueToNull( NodeId.from( "d" ) );
        setManualOrderValueToNull( NodeId.from( "e" ) );
        setManualOrderValueToNull( NodeId.from( "f" ) );

        assertThrows( IllegalArgumentException.class, () -> ReorderChildNodesCommand.create()
            .params( ReorderChildNodesParams.create()
                         .add( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "f" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute() );
    }

    private void setManualOrderValueToNull( final NodeId nodeId )
    {
        updateNode( UpdateNodeParams.create().
            editor( ( node ) -> {
                node.manualOrderValue = null;
            } ).
            id( nodeId ).
            build() );
    }

    private void createChildNodes( final Node parentNode )
    {
        createNode( parentNode.path(), "b"  );
        createNode( parentNode.path(), "a" );
        createNode( parentNode.path(), "c" );
        createNode( parentNode.path(), "f" );
        createNode( parentNode.path(), "e" );
        createNode( parentNode.path(), "d" );
    }

    private void setChildOrder( final Node parentNode, final IndexPath indexPath, final OrderExpr.Direction direction )
    {
        SetNodeChildOrderCommand.create().
            nodeId( parentNode.id() ).
            childOrder( ChildOrder.create().add( FieldOrderExpr.create( indexPath, direction ) ).build() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            refresh( RefreshMode.ALL ).
            build().
            execute();
    }
}


