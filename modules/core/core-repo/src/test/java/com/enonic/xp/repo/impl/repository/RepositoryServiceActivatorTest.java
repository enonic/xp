package com.enonic.xp.repo.impl.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<?> service;

    @Mock(stubOnly = true)
    private RepositoryEntryService repositoryEntryService;

    @Mock(stubOnly = true)
    private IndexServiceInternal indexServiceInternal;

    @Mock(stubOnly = true)
    private NodeRepositoryService nodeRepositoryService;

    @Mock(stubOnly = true)
    private NodeStorageService nodeStorageService;

    @Mock(stubOnly = true)
    private NodeSearchService nodeSearchService;

    @BeforeEach
    void setUp()
    {
        when( indexServiceInternal.isMaster() ).thenReturn( true );
        when( indexServiceInternal.waitForYellowStatus() ).thenReturn( true );
        when( indexServiceInternal.indicesExists( any() ) ).thenReturn( true );

        final Node mockNode = Node.create().id( NodeId.from( "1" ) ).parentPath( NodePath.ROOT ).build();
        when( nodeStorageService.store( any(), any() ) ).thenReturn( new NodeVersionData( mockNode, mock( NodeVersionMetadata.class ) ) );
    }

    @Test
    void lifecycle()
    {
        final RepositoryServiceActivator activator =
            new RepositoryServiceActivator( repositoryEntryService, indexServiceInternal, nodeRepositoryService, nodeStorageService,
                                            nodeSearchService );

        doReturn( service ).when( bundleContext ).registerService(
            AdditionalMatchers.aryEq( new String[]{RepositoryService.class.getName(), InternalRepositoryService.class.getName()} ),
            any( RepositoryService.class ), isNull() );

        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }
}
