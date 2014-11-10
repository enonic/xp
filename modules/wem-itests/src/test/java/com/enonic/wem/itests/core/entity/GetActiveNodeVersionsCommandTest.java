package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.GetActiveNodeVersionsCommand;
import com.enonic.wem.core.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersion;
import com.enonic.wem.core.entity.PushNodeCommand;
import com.enonic.wem.core.entity.UpdateNodeCommand;
import com.enonic.wem.core.entity.UpdateNodeParams;

import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
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
    public void get_active_versions()
        throws Exception
    {
        final Workspace testWorkspace = Workspace.create().
            name( "test-workspace" ).
            build();

        final Context testContext = ContextBuilder.create().
            object( testWorkspace ).
            object( ContentConstants.CONTENT_REPO.getId() ).
            build();

        final Node node = createAndPushNode( testWorkspace );

        ImmutableMap<Workspace, NodeVersion> activeNodeVersions = getVersionsMap( testWorkspace, testContext, node );

        final NodeVersion stageVersion = activeNodeVersions.get( ContentConstants.WORKSPACE_STAGE );
        final NodeVersion testVersion = activeNodeVersions.get( testWorkspace );
        assertNotNull( testVersion );
        assertNotNull( stageVersion );
        assertTrue( stageVersion.equals( testVersion ) );

        updateNode( node );

        activeNodeVersions = getVersionsMap( testWorkspace, testContext, node );

        final NodeVersion newStageVersion = activeNodeVersions.get( ContentConstants.WORKSPACE_STAGE );
        final NodeVersion newTestVersion = activeNodeVersions.get( testWorkspace );
        assertNotNull( newTestVersion );
        assertNotNull( newStageVersion );
        assertFalse( newStageVersion.equals( newTestVersion ) );
        assertTrue( newStageVersion.getTimestamp().isAfter( newTestVersion.getTimestamp() ) );
    }

    private ImmutableMap<Workspace, NodeVersion> getVersionsMap( final Workspace testWorkspace, final Context testContext, final Node node )
    {
        GetActiveNodeVersionsResult result = doGetActiveVersions( testWorkspace, testContext, node );
        return result.getNodeVersions();
    }

    private void updateNode( final Node node )
    {
        UpdateNodeCommand.create().
            params( new UpdateNodeParams().id( node.id() ).
                editor( toBeEdited -> Node.editNode( toBeEdited ).
                    name( NodeName.from( toBeEdited.name().toString() + "-edit" ) ) ) ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            build().
            execute();
    }

    private Node createAndPushNode( final Workspace testWorkspace )
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        PushNodeCommand.create().
            id( node.id() ).
            target( testWorkspace ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            build().
            execute();
        return node;
    }

    private GetActiveNodeVersionsResult doGetActiveVersions( final Workspace testWorkspace, final Context testContext, final Node node )
    {
        return testContext.runWith( () -> GetActiveNodeVersionsCommand.create().
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            nodeId( node.id() ).
            workspaces( Workspaces.from( ContentConstants.WORKSPACE_STAGE, testWorkspace ) ).
            build().
            execute() );
    }


}



