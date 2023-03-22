package com.enonic.xp.server.impl.status.check;

import java.util.List;

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
public class OSGIReadyCheckTest
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "com.enonic.xp.export.ExportService", "com.enonic.xp.scheduler.SchedulerService",
                 "com.enonic.xp.portal.websocket.WebSocketManager", "com.enonic.xp.portal.view.ViewFunctionService",
                 "com.enonic.xp.task.TaskService", "com.enonic.xp.task.TaskDescriptorService",
                 "com.enonic.xp.script.event.ScriptEventManager", "com.enonic.xp.security.SecurityService",
                 "com.enonic.xp.portal.url.PortalUrlService", "com.enonic.xp.web.multipart.MultipartService",
                 "com.enonic.xp.mail.MailService", "com.enonic.xp.i18n.LocaleService", "com.enonic.xp.portal.owasp.HtmlSanitizer",
                 "com.enonic.xp.schema.content.ContentTypeService", "com.enonic.xp.admin.tool.AdminToolDescriptorService",
                 "com.enonic.xp.server.internal.deploy.DeployDirectoryWatcher", "com.enonic.xp.server.internal.deploy.AutoDeployer",
                 "com.enonic.xp.server.internal.deploy.StoredApplicationsDeployer" );

    @Mock
    private BundleContext bundleContext;

    @Test
    public void testNotReady()
    {
        final StateCheck healthCheck = new ReadyOSGIStateCheck( bundleContext );
        final StateCheckResult result = healthCheck.check();

        assertThat( result.getErrorMessages() ).containsOnly( "[com.enonic.xp.portal.url.PortalUrlService] service in not available",
                                                              "[com.enonic.xp.admin.tool.AdminToolDescriptorService] service in not available",
                                                              "[com.enonic.xp.task.TaskDescriptorService] service in not available",
                                                              "[com.enonic.xp.portal.view.ViewFunctionService] service in not available",
                                                              "[com.enonic.xp.schema.content.ContentTypeService] service in not available",
                                                              "[com.enonic.xp.server.internal.deploy.AutoDeployer] service in not available",
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
    public void testReady()
        throws Exception
    {
        for ( String s : TRACKED_SERVICE_NAMES )
        {
            final ServiceReference<Object> serviceMock = mock( ServiceReference.class );

            when( bundleContext.getServiceReferences( eq( s ), isNull() ) ).thenReturn( new ServiceReference<?>[]{serviceMock} );
            when( bundleContext.getService( serviceMock ) ).thenReturn( mock( Object.class ) );
        }

        final StateCheck healthCheck = new ReadyOSGIStateCheck( bundleContext );
        final StateCheckResult result = healthCheck.check();

        assertTrue( result.getErrorMessages().isEmpty() );
    }
}
