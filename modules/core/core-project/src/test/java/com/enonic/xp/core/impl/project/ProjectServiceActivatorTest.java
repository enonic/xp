package com.enonic.xp.core.impl.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SecurityService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<ProjectService> service;

    @Mock(stubOnly = true)
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private IndexService indexService;

    @Mock(stubOnly = true)
    private RepositoryService repositoryService;

    @Mock(stubOnly = true)
    SecurityService securityService;

    @Mock(stubOnly = true)
    ProjectPermissionsContextManager projectPermissionsContextManager;

    @BeforeEach
    void setUp()
    {
        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );

        final Node mockNode = Node.create().id( NodeId.from( "1" ) ).parentPath( NodePath.ROOT ).build();
        when( nodeService.create( any() ) ).thenReturn( mockNode );
    }

    @Test
    void lifecycle()
    {
        final ProjectServiceActivator activator =
            new ProjectServiceActivator( repositoryService, indexService, nodeService, securityService, projectPermissionsContextManager );

        when( bundleContext.registerService( same( ProjectService.class ), any( ProjectService.class ), isNull() ) ).
            thenReturn( service );

        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }
}
