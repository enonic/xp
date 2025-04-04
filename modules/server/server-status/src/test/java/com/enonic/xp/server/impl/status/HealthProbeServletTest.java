package com.enonic.xp.server.impl.status;

import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HealthProbeServletTest
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "org.elasticsearch.client.Client", "org.elasticsearch.client.AdminClient", "org.elasticsearch.client.ClusterAdminClient" );

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
    public void testHealthy()
        throws Exception
    {
        for ( String s : TRACKED_SERVICE_NAMES )
        {
            final ServiceReference<Object> serviceMock = mock( ServiceReference.class );

            when( bundleContext.getServiceReferences( eq( s ), isNull() ) ).thenReturn( new ServiceReference<?>[]{serviceMock} );
            when( bundleContext.getService( serviceMock ) ).thenReturn( mock( Object.class ) );
        }

        final HealthProbeServlet servlet = new HealthProbeServlet( bundleContext );
        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 200 ) );

        servlet.deactivate();
        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 503 ) );
    }

    @Test
    public void testUnhealthy()
        throws Exception
    {
        final HealthProbeServlet servlet = new HealthProbeServlet( bundleContext );

        servlet.doGet( req, res );

        verify( res ).setStatus( eq( 503 ) );
    }
}
