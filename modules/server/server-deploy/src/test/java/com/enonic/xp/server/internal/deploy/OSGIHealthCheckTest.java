package com.enonic.xp.server.internal.deploy;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.server.internal.deploy.health.OSGIHealthCheck;
import com.enonic.xp.status.health.HealthCheckResult;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OSGIHealthCheckTest
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

    private OSGIHealthCheck healthCheck;

    @Mock
    private BundleContext bundleContext;


    @BeforeEach
    public void setup()
    {
    }

    @Test
    public void testUnhealthy()
    {
        this.healthCheck = new OSGIHealthCheck( bundleContext );

        final HealthCheckResult result = this.healthCheck.isHealthy();

        Assertions.assertTrue( result.isNotHealthy() );
        Assertions.assertEquals( 18, result.getErrorMessages().size() );
    }

    @Test
    public void testHealthy()
    {
        TRACKED_SERVICE_NAMES.forEach( s -> {
            try
            {
                final ServiceReference<Object> serviceMock = mock( ServiceReference.class );

                when( bundleContext.getServiceReferences( eq( s ), isNull() ) ).thenReturn( new ServiceReference<?>[]{serviceMock} );
                when( bundleContext.getService( serviceMock ) ).thenReturn( mock( Class.forName( s ) ) );
            }
            catch ( InvalidSyntaxException | ClassNotFoundException e )
            {
                throw new RuntimeException( e );
            }
        } );

        this.healthCheck = new OSGIHealthCheck( bundleContext );

        final HealthCheckResult result = this.healthCheck.isHealthy();

        Assertions.assertTrue( result.isHealthy() );
    }


}
