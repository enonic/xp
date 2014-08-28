package com.enonic.wem.core.entity;

import java.time.Instant;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.version.VersionService;

import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
{
    private VersionService versionService;

    private NodeDao nodeDao;


    @Before
    public void setUp()
        throws Exception
    {
        this.versionService = Mockito.mock( VersionService.class );
        this.nodeDao = Mockito.mock( NodeDao.class );
    }

    @Test
    public void getFromMultipleWorkspaces()
        throws Exception
    {
        final EntityId nodeId = EntityId.from( "1" );

        final Workspace testWorkspace = Workspace.from( "test" );
        final Workspace prodWorkspace = Workspace.from( "prod" );
        final Context testContext = Context.create( testWorkspace );
        final Set<Workspace> workspaces = Sets.newHashSet( testWorkspace, prodWorkspace );

        final BlobKey testBlobKey = new BlobKey( "a" );
        final BlobKey prodBlobKey = new BlobKey( "b" );
        final NodeVersion testVersion = new NodeVersion( testBlobKey, Instant.now() );
        final NodeVersion prodVersion = new NodeVersion( prodBlobKey, Instant.now() );

        Mockito.when( this.nodeDao.getBlobKey( nodeId, testWorkspace ) ).thenReturn( testBlobKey );
        Mockito.when( this.nodeDao.getBlobKey( nodeId, prodWorkspace ) ).thenReturn( prodBlobKey );
        Mockito.when( this.versionService.getVersion( testBlobKey ) ).thenReturn( testVersion );
        Mockito.when( this.versionService.getVersion( prodBlobKey ) ).thenReturn( prodVersion );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create( testContext ).
            versionService( this.versionService ).
            nodeDao( nodeDao ).
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
        final Context testContext = Context.create( testWorkspace );
        final Set<Workspace> workspaces = Sets.newHashSet( testWorkspace, prodWorkspace );

        final BlobKey testBlobKey = new BlobKey( "a" );
        final NodeVersion testVersion = new NodeVersion( testBlobKey, Instant.now() );

        Mockito.when( this.nodeDao.getBlobKey( nodeId, testWorkspace ) ).thenReturn( testBlobKey );
        Mockito.when( this.nodeDao.getBlobKey( nodeId, prodWorkspace ) ).thenThrow( new NodeNotFoundException( "expected" ) );
        Mockito.when( this.versionService.getVersion( testBlobKey ) ).thenReturn( testVersion );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create( testContext ).
            versionService( this.versionService ).
            nodeDao( nodeDao ).
            entityId( nodeId ).
            workspaces( workspaces ).
            build().
            execute();

        assertEquals( 1, result.getNodeVersions().size() );
        assertEquals( testVersion, result.getNodeVersions().get( testWorkspace ) );
    }
}