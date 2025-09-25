package com.enonic.xp.core.node;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.node.DuplicateNodeCommand;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;

@Disabled("Performance test is only for manual run")
public class DuplicateNodeCommandPerformanceTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void testDuplicatePerformance()
        throws Exception
    {
        final Node rootNode = createNodeSkipVerification( CreateNodeParams.create().name( "rootNode" ).parent( NodePath.ROOT ).build() );

        createNodes( rootNode, 30, 3, 1 );

        refresh();

        final NodeQuery query = NodeQuery.create().size( 0 ).build();

        final FindNodesByQueryResult result = FindNodesByQueryCommand.create().query( query ).searchService( this.searchService ).storageService( this.storageService ).indexServiceInternal( this.indexServiceInternal ).build().execute();

        final Stopwatch started = Stopwatch.createStarted();

        DuplicateNodeCommand.create().params( DuplicateNodeParams.create().nodeId( rootNode.id() ).build() ).indexServiceInternal( this.indexServiceInternal ).storageService( this.storageService ).searchService( this.searchService ).binaryService( this.binaryService ).build().execute();

        started.stop();

        final long elapsed = started.elapsed( TimeUnit.SECONDS );

        System.out.println( "Duplicated [" + result.getTotalHits() + "] in " + started );
    }


}
