package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CompareNodesCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompareNodesCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void compare_nodes()
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

        pushNodes( WS_OTHER, node.id(), node2.id() );

        doDeleteNode( node2.id() );

        refresh();

        final NodeComparisons result = CompareNodesCommand.create().
            nodeIds( NodeIds.from( node.id(), node2.id(), node3.id() ) ).
            storageService( this.storageService ).
            target( WS_OTHER ).
            build().
            execute();

        assertEquals( 1, result.getWithStatus( CompareStatus.NEW_TARGET ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.NEW ).size() );
        assertEquals( 1, result.getWithStatus( CompareStatus.EQUAL ).size() );
    }
}
