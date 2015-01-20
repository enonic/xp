package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.GetActiveNodeVersionsResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersion;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.workspace.Workspaces;

import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
    extends AbstractNodeTest
{
    @Test
    public void get_active_versions()
        throws Exception
    {

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNodes( NodeIds.from( node.id() ), WS_OTHER );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create().
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodeId( node.id() ).
            workspaces( Workspaces.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        NodeVersion stage = result.getNodeVersions().get( WS_DEFAULT );
        NodeVersion prod = result.getNodeVersions().get( WS_OTHER );

        assertEquals( stage, prod );

        updateNode( node, CTX_DEFAULT );

        final GetActiveNodeVersionsResult result2 = GetActiveNodeVersionsCommand.create().
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodeId( node.id() ).
            workspaces( Workspaces.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        stage = result2.getNodeVersions().get( WS_DEFAULT );
        prod = result2.getNodeVersions().get( WS_OTHER );

        assertTrue( !stage.equals( prod ) );
        assertTrue( stage.getTimestamp().isAfter( prod.getTimestamp() ) );
    }

    private void updateNode( final Node node, Context context )
    {
        UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.name = NodeName.from( toBeEdited.name.toString() + "-edit" ) ).
            build();

        context.runWith( () -> UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute() );
    }
}



