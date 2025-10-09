package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindNodesByQueryCommandTest_references
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void incomingReferences()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            build() );

        createNodeWithReference( "node2", node1.id() );
        createNodeWithReference( "node3", node1.id() );
        createNodeWithReference( "node4", node1.id() );

        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( "_references = 'node1'" ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 3, result.getNodeHits().getSize() );

        assertTrue( result.getNodeIds().contains( NodeId.from( "node2" ) ) );
        assertTrue( result.getNodeIds().contains( NodeId.from( "node3" ) ) );
        assertTrue( result.getNodeIds().contains( NodeId.from( "node4" ) ) );
    }

    private Node createNodeWithReference( final String name, final NodeId ref )
    {
        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( ref.toString() ) );

        return createNode( CreateNodeParams.create().
            name( name ).
            setNodeId( NodeId.from( name ) ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );
    }
}
