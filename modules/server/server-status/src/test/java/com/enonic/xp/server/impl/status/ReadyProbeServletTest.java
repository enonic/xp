package com.enonic.xp.server.impl.status;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadyProbeServletTest
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

    @Mock(stubOnly = true)
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse res;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private BundleContext bundleContext;

    @BeforeEach
    public void activate()
        throws Exception
    {
        when( res.getWriter() ).thenReturn( printWriter );
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

        final ReadyProbeServlet servlet = new ReadyProbeServlet( bundleContext );
        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 200 ) );

        servlet.deactivate();
        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 503 ) );
    }

    @Test
    public void testNotReady()
        throws Exception
    {
        final ReadyProbeServlet servlet = new ReadyProbeServlet( bundleContext );

        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 503 ) );
    }
}
