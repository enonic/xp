package com.enonic.xp.repo.impl.storage;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.commit.CommitService;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class NodeStorageServiceImplTest
{
    private NodeStorageServiceImpl instance;

    private VersionService versionService;

    private NodeVersionService nodeVersionService;

    private NodeVersionId nodeVersionId;

    private InternalContext context;

    private NodeVersionKey versionKey;

    @BeforeEach
    void setUp()
    {
        versionService = mock( VersionService.class );
        nodeVersionService = mock( NodeVersionService.class );
        BranchService branchService = mock( BranchService.class );
        CommitService commitService = mock( CommitService.class );
        IndexDataService indexDataService = mock( IndexDataService.class );

        instance = new NodeStorageServiceImpl( versionService, branchService, commitService, nodeVersionService, indexDataService );

        nodeVersionId = NodeVersionId.from( "000-000-000-000" );

        versionKey = mock( NodeVersionKey.class );

        context = InternalContext.create().
            repositoryId( RepositoryId.from( "repository-id" ) ).
            principalsKeys( AuthenticationInfo.unAuthenticated().getPrincipals() ).
            branch( Branch.from( "branch" ) ).
            build();
    }

    @Test
    void testGetNode()
    {
        final NodeVersionMetadata nodeVersionMetadata = NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionKey( versionKey ).
            binaryBlobKeys( BlobKeys.empty() ).
            nodeVersionId( nodeVersionId ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.EPOCH ).
            build();

        final NodeVersion nodeVersion = NodeVersion.create().
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( RoleKeys.EVERYONE ).
                    allow( Permission.READ ).
                    build() ).build() ).
            build();

        when( versionService.getVersion( any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( nodeVersionMetadata );
        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( nodeVersion );

        final Node result = instance.get( nodeVersionId, context );

        assertNotNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( versionService, nodeVersionService );
    }

    @Test
    void testGetNode_NodeVersionMetadataNotFound()
    {
        when( versionService.getVersion( any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( null );

        final Node result = instance.get( nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeVersionId.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( versionService );
    }

    @Test
    void testGetNode_NodeVersionNotFound()
    {
        when( versionService.getVersion( any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( NodeVersionMetadata.create().
            nodeId( NodeId.from( "nodeId1" ) ).
            nodeVersionKey( versionKey ).
            binaryBlobKeys( BlobKeys.empty() ).
            nodeVersionId( nodeVersionId ).
            nodePath( NodePath.ROOT ).
            timestamp( Instant.EPOCH ).
            build() );

        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( null );

        final Node result = instance.get( nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );

        verifyNoMoreInteractions( versionService, nodeVersionService );
    }
}
