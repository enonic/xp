package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.Assert.*;

public class GetNodeVersionsCommandTest
    extends AbstractNodeTest
{
    private final Random random = new Random();

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void get_single_version()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final NodeVersionQueryResult result = GetNodeVersionsCommand.create().
            from( 0 ).
            size( 100 ).
            nodeId( node.id() ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 1, result.getHits() );
    }

    @Test
    public void get_multiple_version()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        doUpdateNode( node );
        doUpdateNode( node );
        doUpdateNode( node );
        doUpdateNode( node );

        refresh();

        final NodeVersionQueryResult result = GetNodeVersionsCommand.create().
            from( 0 ).
            size( 100 ).
            nodeId( node.id() ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 5, result.getHits() );

        final NodeVersions nodeVersions = result.getNodeVersions();
        Instant previousTimestamp = null;
        for ( final NodeVersion nodeVersion : nodeVersions )
        {
            assertTrue( previousTimestamp == null || nodeVersion.getTimestamp().isBefore( previousTimestamp ) );
            previousTimestamp = nodeVersion.getTimestamp();
        }
    }

    private Node doUpdateNode( final Node node )
    {
        UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.name = NodeName.from( node.name() + "-" + this.random.nextLong() ) ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }
}
