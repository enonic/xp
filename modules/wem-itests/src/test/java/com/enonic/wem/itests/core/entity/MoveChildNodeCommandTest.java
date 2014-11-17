package com.enonic.wem.itests.core.entity;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.repo.internal.entity.MoveChildNodeCommand;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.repo.internal.entity.SetNodeChildOrderCommand;

import static org.junit.Assert.*;

public class MoveChildNodeCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
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

        setChildOrder( parentNode, "name", OrderExpr.Direction.ASC );
        setChildOrder( parentNode, "manualOrderValue", OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        MoveChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            nodeToMoveBefore( getNodeById( NodeId.from( "a" ) ) ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
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

        setChildOrder( parentNode, "name", OrderExpr.Direction.ASC );
        setChildOrder( parentNode, "manualOrderValue", OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        MoveChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            nodeToMoveBefore( getNodeById( NodeId.from( "b" ) ) ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
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

        setChildOrder( parentNode, "name", OrderExpr.Direction.ASC );
        setChildOrder( parentNode, "manualOrderValue", OrderExpr.Direction.DESC );

        // current node order: a,b,c,d,e,f
        MoveChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( NodeId.from( "c" ) ) ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
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

    private void setChildOrder( final Node parentNode, final String fieldName, final OrderExpr.Direction direction )
    {
        SetNodeChildOrderCommand.create().
            nodeId( parentNode.id() ).
            childOrder( ChildOrder.create().add( FieldOrderExpr.create( fieldName, direction ) ).build() ).
            nodeDao( nodeDao ).
            versionService( versionService ).
            workspaceService( workspaceService ).
            queryService( queryService ).
            indexService( indexService ).
            build().
            execute();

        refresh();
    }

    private Node createNode( final String name, final NodePath parent )
    {
        final RootDataSet data = new RootDataSet();
        data.setProperty( "displayName", Value.newString( name ) );

        return createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( name ) ).
            name( name ).
            parent( parent ).
            data( data ).
            build() );
    }
}


