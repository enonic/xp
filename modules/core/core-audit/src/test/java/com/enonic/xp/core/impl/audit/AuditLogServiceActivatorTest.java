package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<AuditLogService> service;

    @Mock(stubOnly = true)
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private IndexService indexService;

    @Mock(stubOnly = true)
    private RepositoryService repositoryService;

    @Mock(stubOnly = true)
    private AuditLogConfig auditLogConfig;

    @BeforeEach
    void setUp()
    {
        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );
    }

    @Test
    void lifecycle()
    {
        final AuditLogServiceActivator activator =
            new AuditLogServiceActivator( auditLogConfig, indexService, repositoryService, nodeService );

        when( bundleContext.registerService( same( AuditLogService.class ), any( AuditLogService.class ), isNull() ) ).
            thenReturn( service );

        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }
}
