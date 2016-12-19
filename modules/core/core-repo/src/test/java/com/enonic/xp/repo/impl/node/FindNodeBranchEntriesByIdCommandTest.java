package com.enonic.xp.repo.impl.node;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;

import static org.junit.Assert.*;

public class FindNodeBranchEntriesByIdCommandTest
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
    public void node_with_capital_letter_in_id()
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "Node1" ) ).
            parent( NodePath.ROOT ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            name( "node1_1" ).
            setNodeId( NodeId.from( "Node1_1" ) ).
            parent( node1.path() ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            name( "node1_1_1" ).
            setNodeId( NodeId.from( "Node1_1_1" ) ).
            parent( node1_1.path() ).
            build() );

        final NodeBranchEntries result = FindNodeBranchEntriesByIdCommand.create().
            ids( NodeIds.from( node1.id(), node1_1.id(), node1_1_1.id() ) ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( indexServiceInternal ).
            build().
            execute();

        assertEquals( 3, result.getSize() );
    }

    @Test
    public void find_order_by_path()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            name( "node1_1" ).
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            build() );

        final Node node1_2 = createNode( CreateNodeParams.create().
            name( "node1_2" ).
            setNodeId( NodeId.from( "node1_2" ) ).
            parent( node1.path() ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            name( "node1_1_1" ).
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            build() );

        refresh();

        final NodeBranchEntries result = FindNodeBranchEntriesByIdCommand.create().
            ids( NodeIds.from( node1_2.id(), node1.id(), node1_1.id(), node1_1_1.id() ) ).
            orderExpressions( OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.PATH, OrderExpr.Direction.ASC ) ) ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( indexServiceInternal ).
            build().
            execute();

        assertEquals( 4, result.getSize() );

        final Iterator<NodeBranchEntry> iterator = result.iterator();

        assertEquals( node1.id(), iterator.next().getNodeId() );
        assertEquals( node1_1.id(), iterator.next().getNodeId() );
        assertEquals( node1_1_1.id(), iterator.next().getNodeId() );
        assertEquals( node1_2.id(), iterator.next().getNodeId() );
    }
}