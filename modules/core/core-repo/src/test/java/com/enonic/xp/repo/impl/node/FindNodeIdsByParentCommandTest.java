package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class FindNodeIdsByParentCommandTest
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
    public void getChildren()
        throws Exception
    {

        final Node root = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( root.path() ).
            name( "node1_1" ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        refresh();

        final NodeIds children = FindNodeIdsByParentCommand.create().
            parentPath( root.path() ).
            recursive( true ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        assertEquals( 2, children.getSize() );
    }

}