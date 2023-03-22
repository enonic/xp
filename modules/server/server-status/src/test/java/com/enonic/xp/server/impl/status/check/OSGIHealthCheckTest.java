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
public class OSGIHealthCheckTest
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "org.elasticsearch.client.Client", "org.elasticsearch.client.AdminClient", "org.elasticsearch.client.ClusterAdminClient" );

    @Mock
    private BundleContext bundleContext;

    @Test
    public void testUnhealthy()
    {
        final StateCheck healthCheck = new HealthOSGIStateCheck( bundleContext );
        final StateCheckResult result = healthCheck.check();

        assertThat( result.getErrorMessages() ).containsOnly( "[org.elasticsearch.client.AdminClient] service in not available",
                                                              "[org.elasticsearch.client.Client] service in not available",
                                                              "[org.elasticsearch.client.ClusterAdminClient] service in not available" );
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

        final StateCheck healthCheck = new HealthOSGIStateCheck( bundleContext );
        final StateCheckResult result = healthCheck.check();

        assertTrue( result.getErrorMessages().isEmpty() );
    }
}
