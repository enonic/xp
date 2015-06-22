package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class CompareNodesCommandTest
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
    public void compare_nodes()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "node1" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "node2" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            name( "node3" ).
            parent( NodePath.ROOT ).
            build() );

        pushNodes( NodeIds.from( node.id(), node2.id() ), WS_OTHER );

        doDeleteNode( node2.id() );

        printBranchIndex();

        final NodeComparisons result = CompareNodesCommand.create().
            nodeIds( NodeIds.from( node.id(), node2.id(), node3.id() ) ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            target( WS_OTHER ).
            build().
            execute();

        assertEquals( 1, result.getWithStatus( CompareStatus.NEW_TARGET ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.NEW ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.EQUAL ).size() );
    }
}