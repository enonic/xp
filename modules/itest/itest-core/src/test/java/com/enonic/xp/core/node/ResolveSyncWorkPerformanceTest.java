package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.node.ResolveSyncWorkCommand;

@Disabled("Performance test is only for manual run")
class ResolveSyncWorkPerformanceTest
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

        createNodes( rootNode, 40, 3, 1 );

        refresh();

        final Stopwatch started = Stopwatch.createStarted();

        final ResolveSyncWorkResult resolvedNodes = ResolveSyncWorkCommand.create().nodeId( rootNode.id() ).target( WS_OTHER ).indexServiceInternal( this.indexServiceInternal ).storageService( this.storageService ).searchService( this.searchService ).build().execute();

        started.stop();

        System.out.println( resolvedNodes.getSize() + " in " + started );
    }

}
