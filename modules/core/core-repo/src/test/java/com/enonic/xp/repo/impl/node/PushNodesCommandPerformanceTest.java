package com.enonic.xp.repo.impl.node;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.InternalContext;

@Disabled("Performance test is only for manual run")
public class PushNodesCommandPerformanceTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
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
        final int number = result.getSuccessful().getSize();

        System.out.println( "Pushed : " + number + " in " + started + ", " + ( elapsed == 0 ? "n/a" : ( number / elapsed ) + "/s" ) );
    }

    @Test
    void testSearchVsMget()
    {
        System.out.format("Creating nodes...%n");

        // create a batch of notes greater than elasticsearch can return in a single query
        final int amountOfNodesToLoad = 100_100;

        NodeIds.Builder nodeIds = NodeIds.create();

        for ( int i = 0; i < amountOfNodesToLoad; i++ )
        {
            final Node node = createNodeSkipVerification( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build() );
            nodeIds.add( node.id() );
            if ((i + 1) % 1000 == 0) {
                System.out.format("Nodes created so far %s %n", i + 1);
            }
        }

        refresh();

        System.out.format("Starting...%n");

        final Stopwatch timer = Stopwatch.createUnstarted();

        for ( int i = 0; i < 10; i++ )
        {
            timer.reset().start();
            NodeBranchEntries result = branchService.get( nodeIds.build(), InternalContext.from( ContextAccessor.current() ) );
            timer.stop();
            final Node node = createNodeSkipVerification( CreateNodeParams.create().name( "extranode" + i ).parent( NodePath.ROOT ).build() );
            nodeIds.add( node.id() );
            System.out.format("Fetched %s in %s%n", result.getSize(), timer);
        }

    }

}
