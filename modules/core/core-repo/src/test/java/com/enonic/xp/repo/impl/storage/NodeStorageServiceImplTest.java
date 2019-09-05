package com.enonic.xp.repo.impl.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeStorageServiceImplTest
{

    @InjectMocks
    private NodeStorageServiceImpl instance;

    @Mock
    private VersionService versionService;

    @Mock
    private NodeVersionService nodeVersionService;

    private NodeId nodeId;

    private NodeVersionId nodeVersionId;

    private InternalContext context;

    @Before
    public void setUp()
    {
        nodeId = NodeId.from( "000-000-000-000" );

        nodeVersionId = NodeVersionId.from( "000-000-000-000" );

        context = InternalContext.create().
            repositoryId( RepositoryId.from( "repository-id" ) ).
            branch( Branch.from( "branch" ) ).
            build();
    }

    @Test
    public void testGetNode()
    {
        final NodePath nodePath = NodePath.create( "path" ).build();

        final NodeVersionMetadata nodeVersionMetadata = NodeVersionMetadata.create().
            nodeVersionId( nodeVersionId ).
            nodePath( nodePath ).
            build();

        final NodeVersion nodeVersion = NodeVersion.create().
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).build() ).
            build();

        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( nodeVersionMetadata );
        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( nodeVersion );

        final Node result = instance.getNode( nodeId, nodeVersionId, context );

        assertNotNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( versionService, nodeVersionService );
    }

    @Test
    public void testGetNode_NodeVersionMetadataNotFound()
    {
        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( null );

        final Node result = instance.getNode( nodeId, nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verifyNoMoreInteractions( versionService );
    }

    @Test
    public void testGetNode_NodeVersionNotFound()
    {
        when( versionService.getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) ) ).
            thenReturn( NodeVersionMetadata.create().build() );

        when( nodeVersionService.get( any( NodeVersionKey.class ), any( InternalContext.class ) ) ).thenReturn( null );

        final Node result = instance.getNode( nodeId, nodeVersionId, context );

        assertNull( result );

        verify( versionService, times( 1 ) ).
            getVersion( any( NodeId.class ), any( NodeVersionId.class ), any( InternalContext.class ) );
        verify( nodeVersionService, times( 1 ) ).
            get( any( NodeVersionKey.class ), any( InternalContext.class ) );

        verifyNoMoreInteractions( versionService, nodeVersionService );
    }


}
