package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.GetActiveNodeVersionsResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
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

        final PropertyTree data = new PropertyTree();
        data.addString( "myString", "value" );

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        pushNodes( NodeIds.from( node.id() ), WS_OTHER );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create().
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodeId( node.id() ).
            workspaces( Workspaces.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        NodeVersion draft = result.getNodeVersions().get( WS_DEFAULT );
        NodeVersion online = result.getNodeVersions().get( WS_OTHER );

        assertEquals( draft, online );

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

        draft = result2.getNodeVersions().get( WS_DEFAULT );
        online = result2.getNodeVersions().get( WS_OTHER );

        assertTrue( !draft.equals( online ) );
        assertTrue( draft.getTimestamp().isAfter( online.getTimestamp() ) );
    }

    private void updateNode( final Node node, Context context )
    {
        UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.data.setString( "myString", "edit" ) ).
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



