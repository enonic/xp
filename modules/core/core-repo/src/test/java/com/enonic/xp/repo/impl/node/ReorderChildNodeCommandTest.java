package com.enonic.xp.repo.impl.node;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

import static org.junit.Assert.*;

public class ReorderChildNodeCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
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
        ReorderChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            nodeToMoveBefore( getNodeById( NodeId.from( "a" ) ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( FindNodesByParentParams.create().
            parentPath( parentNode.path() ).
            build() );

        // updated node order: c,a,b,d,e,f
        final Iterator<Node> iterator = reOrderedResult.getNodes().iterator();
        assertEquals( "c", iterator.next().id().toString() );
        assertEquals( "a", iterator.next().id().toString() );
        assertEquals( "b", iterator.next().id().toString() );
        assertEquals( "d", iterator.next().id().toString() );
        assertEquals( "e", iterator.next().id().toString() );
        assertEquals( "f", iterator.next().id().toString() );
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
        ReorderChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            nodeToMoveBefore( getNodeById( NodeId.from( "b" ) ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( FindNodesByParentParams.create().
            parentPath( parentNode.path() ).
            build() );

        // updated node order: a,c,b,d,e,f
        final Iterator<Node> iterator = reOrderedResult.getNodes().iterator();
        assertEquals( "a", iterator.next().id().toString() );
        assertEquals( "c", iterator.next().id().toString() );
        assertEquals( "b", iterator.next().id().toString() );
        assertEquals( "d", iterator.next().id().toString() );
        assertEquals( "e", iterator.next().id().toString() );
        assertEquals( "f", iterator.next().id().toString() );
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
        ReorderChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();
        refresh();

        final FindNodesByParentResult reOrderedResult = findByParent( FindNodesByParentParams.create().
            parentPath( parentNode.path() ).
            build() );

        // updated node order: a,b,d,e,f,c
        final Iterator<Node> iterator = reOrderedResult.getNodes().iterator();
        assertEquals( "a", iterator.next().id().toString() );
        assertEquals( "b", iterator.next().id().toString() );
        assertEquals( "d", iterator.next().id().toString() );
        assertEquals( "e", iterator.next().id().toString() );
        assertEquals( "f", iterator.next().id().toString() );
        assertEquals( "c", iterator.next().id().toString() );
    }

    private void createChildNodes( final Node parentNode )
    {
        createNode( "b", parentNode.path() );
        createNode( "a", parentNode.path() );
        createNode( "c", parentNode.path() );
        createNode( "f", parentNode.path() );
        createNode( "e", parentNode.path() );
        createNode( "d", parentNode.path() );
    }

    private void setChildOrder( final Node parentNode, final IndexPath indexPath, final OrderExpr.Direction direction )
    {
        SetNodeChildOrderCommand.create().
            nodeId( parentNode.id() ).
            childOrder( ChildOrder.create().add( FieldOrderExpr.create( indexPath, direction ) ).build() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        refresh();
    }

    private Node createNode( final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "displayName", name );

        return createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( name ) ).
            name( name ).
            parent( parent ).
            data( data ).
            build() );
    }
}


