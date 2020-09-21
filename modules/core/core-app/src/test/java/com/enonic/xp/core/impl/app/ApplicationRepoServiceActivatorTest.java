package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationRepoServiceActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<ApplicationRepoService> service;

    @Mock(stubOnly = true)
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private IndexService indexService;

    @BeforeEach
    void setUp()
    {
        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );
    }

    @Test
    void lifecycle()
    {
        final ApplicationRepoServiceActivator activator = new ApplicationRepoServiceActivator( nodeService, indexService );

        when( bundleContext.registerService( same( ApplicationRepoService.class ), any( ApplicationRepoService.class ), isNull() ) ).
            thenReturn( service );

        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }
}
