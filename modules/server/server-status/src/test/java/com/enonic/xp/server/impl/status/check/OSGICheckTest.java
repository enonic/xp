package com.enonic.xp.server.impl.status.check;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OSGICheckTest
{
    @Mock
    private BundleContext bundleContext;

    @Test
    public void testNotReady()
    {
        final OSGIStateCheck healthCheck = new OSGIStateCheck( bundleContext, OSGIStateChecks.READY_SERVICE_NAMES );
        final StateCheckResult result = healthCheck.check();

        assertThat( result.getErrorMessages() ).containsOnly( "[com.enonic.xp.portal.url.PortalUrlService] service in not available",
                                                              "[com.enonic.xp.admin.tool.AdminToolDescriptorService] service in not available",
                                                              "[com.enonic.xp.admin.tool.WidgetDescriptorService] service in not available",
                                                              "[com.enonic.xp.task.TaskDescriptorService] service in not available",
                                                              "[com.enonic.xp.portal.view.ViewFunctionService] service in not available",
                                                              "[com.enonic.xp.schema.content.ContentTypeService] service in not available",
                                                              "[com.enonic.xp.script.event.ScriptEventManager] service in not available",
                                                              "[com.enonic.xp.scheduler.SchedulerService] service in not available",
                                                              "[com.enonic.xp.task.TaskService] service in not available",
                                                              "[com.enonic.xp.portal.owasp.HtmlSanitizer] service in not available",
                                                              "[com.enonic.xp.security.SecurityService] service in not available",
                                                              "[com.enonic.xp.server.internal.deploy.StoredApplicationsDeployer] service in not available",
                                                              "[com.enonic.xp.i18n.LocaleService] service in not available",
                                                              "[com.enonic.xp.export.ExportService] service in not available",
                                                              "[com.enonic.xp.portal.websocket.WebSocketManager] service in not available",
                                                              "[com.enonic.xp.web.multipart.MultipartService] service in not available",
                                                              "[com.enonic.xp.mail.MailService] service in not available",
                                                              "[com.enonic.xp.server.internal.deploy.DeployDirectoryWatcher] service in not available" );
    }

    @Test
    public void testNotAlive()
    {
        final OSGIStateCheck healthCheck = new OSGIStateCheck( bundleContext, OSGIStateChecks.LIVE_SERVICE_NAMES );
        final StateCheckResult result = healthCheck.check();

        assertThat( result.getErrorMessages() ).containsOnly( "[org.elasticsearch.client.AdminClient] service in not available",
                                                              "[org.elasticsearch.client.Client] service in not available",
                                                              "[org.elasticsearch.client.ClusterAdminClient] service in not available" );
    }

    @Test
    public void testReady()
        throws Exception
    {
        for ( String s : OSGIStateChecks.READY_SERVICE_NAMES )
        {
            final ServiceReference<Object> serviceMock = mock( ServiceReference.class );

            when( bundleContext.getServiceReferences( eq( s ), isNull() ) ).thenReturn( new ServiceReference<?>[]{serviceMock} );
            when( bundleContext.getService( serviceMock ) ).thenReturn( mock( Object.class ) );
        }

        final OSGIStateCheck healthCheck = new OSGIStateCheck( bundleContext, OSGIStateChecks.READY_SERVICE_NAMES );
        final StateCheckResult result = healthCheck.check();

        assertTrue( result.getErrorMessages().isEmpty() );
    }

    @Test
    public void testAlive()
        throws Exception
    {
        for ( String s : OSGIStateChecks.LIVE_SERVICE_NAMES )
        {
            final ServiceReference<Object> serviceMock = mock( ServiceReference.class );

            when( bundleContext.getServiceReferences( eq( s ), isNull() ) ).thenReturn( new ServiceReference<?>[]{serviceMock} );
            when( bundleContext.getService( serviceMock ) ).thenReturn( mock( Object.class ) );
        }

        final OSGIStateCheck healthCheck = new OSGIStateCheck( bundleContext, OSGIStateChecks.LIVE_SERVICE_NAMES );
        final StateCheckResult result = healthCheck.check();

        assertTrue( result.getErrorMessages().isEmpty() );
    }
}
