package com.enonic.xp.core.node;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CompareNodesCommand;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat( result.getComparisons() ).map( NodeComparison::getCompareStatus )
            .containsOnlyOnceElementsOf( List.of( NodeCompareStatus.NEW_TARGET, NodeCompareStatus.NEW, NodeCompareStatus.EQUAL ) );
    }
}
