package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.NodeVersions;
import com.enonic.wem.api.node.UpdateNodeParams;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class GetNodeVersionsCommandTest
    extends AbstractNodeTest
{

    @Test
    public void get_single_version()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final FindNodeVersionsResult result = GetNodeVersionsCommand.create().
            from( 0 ).
            size( 100 ).
            nodeId( node.id() ).
            versionService( this.versionService ).
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

        final FindNodeVersionsResult result = GetNodeVersionsCommand.create().
            from( 0 ).
            size( 100 ).
            nodeId( node.id() ).
            versionService( this.versionService ).
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
        return UpdateNodeCommand.create().
            params( new UpdateNodeParams().editor( toBeEdited -> Node.editNode( toBeEdited ).
                manualOrderValue( 10l ) ).
                id( node.id() ) ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }


}
