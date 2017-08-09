package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;

public class DeleteNodeByIdsCommandPerformanceTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Ignore
    @Test
    public void deleteNodeByIds()
        throws Exception
    {
        final Node rootNode = createNode( CreateNodeParams.create().
            name( "rootNode" ).
            parent( NodePath.ROOT ).
            build(), false );

        createNodes( rootNode, 50, 3, 1 );

        refresh();

        final NodeQuery query = NodeQuery.create().
            searchMode( SearchMode.COUNT ).
            build();

        final FindNodesByQueryResult result = FindNodesByQueryCommand.create().
            query( query ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final Stopwatch started = Stopwatch.createStarted();

        DeleteNodeByIdCommand.create().
            nodeId( rootNode.id() ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        started.stop();

        System.out.println( "Deleted [" + result.getTotalHits() + "] in " + started );
    }

}
