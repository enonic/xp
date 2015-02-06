package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparisons;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;

import static junit.framework.Assert.assertEquals;

public class CompareNodesCommandTest
    extends AbstractNodeTest
{
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

        assertEquals( 1, result.getWithStatus( CompareStatus.Status.NEW_TARGET ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.Status.NEW ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.Status.EQUAL ).size() );
    }
}