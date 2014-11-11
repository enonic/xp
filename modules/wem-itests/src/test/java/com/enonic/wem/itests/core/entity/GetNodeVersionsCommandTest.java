package com.enonic.wem.itests.core.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodeVersionsResult;
import com.enonic.wem.core.entity.GetNodeVersionsCommand;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersion;
import com.enonic.wem.core.entity.NodeVersions;
import com.enonic.wem.core.entity.UpdateNodeCommand;
import com.enonic.wem.core.entity.UpdateNodeParams;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class GetNodeVersionsCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createContentRepository();
    }

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
