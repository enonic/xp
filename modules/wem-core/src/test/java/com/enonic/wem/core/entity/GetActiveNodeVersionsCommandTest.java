package com.enonic.wem.core.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

import static com.enonic.wem.core.TestContext.TEST_CONTEXT;
import static com.enonic.wem.core.TestContext.TEST_REPOSITORY;
import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
{
    private NodeDao nodeDao;

    private VersionService versionService;

    private WorkspaceService workspaceService;

    @Before
    public void setUp()
        throws Exception
    {
        this.versionService = Mockito.mock( VersionService.class );
        this.workspaceService = Mockito.mock( WorkspaceService.class );
        this.nodeDao = Mockito.mock( NodeDao.class );
    }

    @Test
    public void get_for_all_given_workspaces()
        throws Exception
    {
        final EntityId id = EntityId.from( "id" );

        final Workspace workspace1 = Workspace.from( "workspace1" );
        final Workspace workspace2 = Workspace.from( "Workspace2" );

        final NodeVersionId nodeVersionId1 = NodeVersionId.from( "a" );
        final NodeVersionId nodeVersionId2 = NodeVersionId.from( "b" );

        Mockito.when( this.workspaceService.getCurrentVersion( id, WorkspaceContext.from( workspace1, TEST_REPOSITORY ) ) ).
            thenReturn( nodeVersionId1 );
        Mockito.when( this.workspaceService.getCurrentVersion( id, WorkspaceContext.from( workspace2, TEST_REPOSITORY ) ) ).
            thenReturn( nodeVersionId2 );

        final Instant version1Timestamp = Instant.now();
        final Instant version2Timestamp = Instant.now();

        Mockito.when( this.versionService.getVersion( Mockito.eq( nodeVersionId1 ), Mockito.eq( TEST_REPOSITORY ) ) ).
            thenReturn( new NodeVersion( nodeVersionId1, version1Timestamp ) );
        Mockito.when( this.versionService.getVersion( Mockito.eq( nodeVersionId2 ), Mockito.eq( TEST_REPOSITORY ) ) ).
            thenReturn( new NodeVersion( nodeVersionId2, version2Timestamp ) );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create( TEST_CONTEXT ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            entityId( id ).
            workspaces( Workspaces.from( workspace1, workspace2 ) ).
            build().
            execute();

        assertEquals( 2, result.getNodeVersions().size() );
        assertEquals( nodeVersionId1, result.getNodeVersions().get( workspace1 ).getId() );
        assertEquals( nodeVersionId2, result.getNodeVersions().get( workspace2 ).getId() );
    }
}