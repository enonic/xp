package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.util.Reference;

public class ResolveSyncWorkPerformanceTest
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
    public void testReferencePerformance()
        throws Exception
    {
        final Node rootNode = createNode( CreateNodeParams.create().
            name( "rootNode" ).
            parent( NodePath.ROOT ).
            build(), false );

        createNodes( rootNode, 30, 3, 1 );

        refresh();

        final Stopwatch started = Stopwatch.createStarted();

        final ResolveSyncWorkResult resolvedNodes = ResolveSyncWorkCommand.create().
            nodeId( rootNode.id() ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        started.stop();

        System.out.println( resolvedNodes.getSize() + " in " + started.toString() );
    }

    private final void createNodes( final Node parent, final int numberOfNodes, final int maxLevels, final int level )
    {
        for ( int i = 0; i < numberOfNodes; i++ )
        {
            final PropertyTree data = new PropertyTree();
            data.addReference( "myRef", new Reference( parent.id() ) );

            final Node node = createNode( CreateNodeParams.create().
                name( "nodeName_" + level + "-" + i ).
                parent( parent.path() ).
                data( data ).
                build(), false );

            if ( level < maxLevels )
            {
                createNodes( node, numberOfNodes, maxLevels, level + 1 );
            }
        }
    }
}
