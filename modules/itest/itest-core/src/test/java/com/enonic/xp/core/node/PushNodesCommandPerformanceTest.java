package com.enonic.xp.core.node;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.ResolveSyncWorkCommand;

@Disabled("Performance test is only for manual run")
class PushNodesCommandPerformanceTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void testReferencePerformance()
    {
        final Node rootNode = createNodeSkipVerification( CreateNodeParams.create().name( "rootNode" ).parent( NodePath.ROOT ).build() );

        createNodes( rootNode, 20, 3, 1 );

        refresh();

        final ResolveSyncWorkResult syncWork = ResolveSyncWorkCommand.create()
            .nodeId( rootNode.id() )
            .target( WS_OTHER )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        final Stopwatch started = Stopwatch.createStarted();

        final PushNodesResult result = PushNodesCommand.create()
            .ids( syncWork.getNodeComparisons().getNodeIds() )
            .target( WS_OTHER )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        started.stop();

        final long elapsed = started.elapsed( TimeUnit.SECONDS );
        final int number = result.getSuccessful().size();

        System.out.println( "Pushed : " + number + " in " + started + ", " + ( elapsed == 0 ? "n/a" : ( number / elapsed ) + "/s" ) );
    }
}
