package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;

import static org.junit.Assert.*;

public class NodeHasChildResolverTest
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
    public void has_children()
        throws Exception
    {

        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "my-child-node" ).
            build() );

        assertTrue( NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( parentNode ) );
    }

    @Test
    public void nodes_has_children()
        throws Exception
    {
        final Node parentNode1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node-1" ).
            build() );

        final Node parentNode2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node-2" ).
            build() );

        final Node parentNode3 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node-3" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( parentNode1.path() ).
            name( "my-child-node-1" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( parentNode2.path() ).
            name( "my-child-node-2" ).
            build() );

        final NodesHasChildrenResult result = NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( Nodes.from( parentNode1, parentNode2, parentNode3 ) );

        assertTrue( result.hasChild( parentNode1.id() ) );
        assertTrue( result.hasChild( parentNode2.id() ) );
        assertFalse( result.hasChild( parentNode3.id() ) );
    }

    @Test
    public void no_children()
        throws Exception
    {

        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        assertFalse( NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( parentNode ) );
    }

}
