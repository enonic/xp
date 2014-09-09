package com.enonic.wem.core.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.Workspaces;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
{

    public static final Workspace TEST_WORKSPACE = Workspace.from( "test" );

    private VersionService versionService;

    private NodeDao nodeDao;

    private WorkspaceService workspaceService;

    private final Context testContext = Context.create().
        workspace( TEST_WORKSPACE ).
        repository( Repository.create().
            id( RepositoryId.from( "testing" ) ).
            workspaces( Workspaces.from( TEST_WORKSPACE ) ).
            build() ).
        build();

    @Before
    public void setUp()
        throws Exception
    {
        this.versionService = Mockito.mock( VersionService.class );
        this.nodeDao = Mockito.mock( NodeDao.class );
        this.workspaceService = Mockito.mock( WorkspaceService.class );
    }

    @Test
    public void getFromMultipleWorkspaces()
        throws Exception
    {
        final EntityId nodeId = EntityId.from( "1" );

        final Workspace testWorkspace = Workspace.from( "test" );
        final Workspace prodWorkspace = Workspace.from( "prod" );
        final Workspaces workspaces = Workspaces.from( testWorkspace, prodWorkspace );

        final NodeVersionId testVersionId = NodeVersionId.from( "a" );
        final NodeVersionId prodVersionId = NodeVersionId.from( "b" );
        final NodeVersion testVersion = new NodeVersion( testVersionId, Instant.now() );
        final NodeVersion prodVersion = new NodeVersion( prodVersionId, Instant.now() );

        Mockito.when( this.workspaceService.getCurrentVersion( WorkspaceIdQuery.create().
            workspace( testWorkspace ).
            repository( testContext.getRepository() ).
            entityId( nodeId ).
            build() ) ).
            thenReturn( testVersionId );
        Mockito.when( this.workspaceService.getCurrentVersion( WorkspaceIdQuery.create().
            workspace( prodWorkspace ).
            repository( testContext.getRepository() ).
            entityId( nodeId ).
            build() ) ).
            thenReturn( prodVersionId );

        Mockito.when( this.versionService.getVersion( testVersionId ) ).
            thenReturn( testVersion );
        Mockito.when( this.versionService.getVersion( prodVersionId ) ).
            thenReturn( prodVersion );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create( testContext ).
            versionService( this.versionService ).
            nodeDao( nodeDao ).
            workspaceService( this.workspaceService ).
            entityId( nodeId ).
            workspaces( workspaces ).
            build().
            execute();

        assertEquals( 2, result.getNodeVersions().size() );
        assertEquals( testVersion, result.getNodeVersions().get( testWorkspace ) );
        assertEquals( prodVersion, result.getNodeVersions().get( prodWorkspace ) );
    }


    @Test
    public void onlyInOneOfTheWorkspaces()
        throws Exception
    {
        final EntityId nodeId = EntityId.from( "1" );

        final Workspace testWorkspace = Workspace.from( "test" );
        final Workspace prodWorkspace = Workspace.from( "prod" );
        final Workspaces workspaces = Workspaces.from( testWorkspace, prodWorkspace );

        final NodeVersionId testVersionId = NodeVersionId.from( "a" );
        final NodeVersion testVersion = new NodeVersion( testVersionId, Instant.now() );

        Mockito.when( this.workspaceService.getCurrentVersion( WorkspaceIdQuery.create().
            workspace( testWorkspace ).
            repository( testContext.getRepository() ).
            entityId( nodeId ).
            build() ) ).
            thenReturn( testVersionId );
        Mockito.when( this.workspaceService.getCurrentVersion( WorkspaceIdQuery.create().
            workspace( prodWorkspace ).
            repository( testContext.getRepository() ).
            entityId( nodeId ).
            build() ) ).
            thenReturn( null );

        Mockito.when( this.versionService.getVersion( testVersionId ) ).thenReturn( testVersion );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create( testContext ).
            versionService( this.versionService ).
            nodeDao( nodeDao ).
            workspaceService( this.workspaceService ).
            entityId( nodeId ).
            workspaces( workspaces ).
            build().
            execute();

        assertEquals( 1, result.getNodeVersions().size() );
        assertEquals( testVersion, result.getNodeVersions().get( testWorkspace ) );
    }
}