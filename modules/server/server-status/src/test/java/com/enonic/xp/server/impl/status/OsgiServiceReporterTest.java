package com.enonic.xp.server.impl.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.fasterxml.jackson.databind.JsonNode;

public class OsgiServiceReporterTest
    extends BaseOsgiReporterTest<OsgiServiceReporter>
{
    public OsgiServiceReporterTest()
    {
        super( "osgi.service" );
    }

    @Override
    protected OsgiServiceReporter newReporter()
        throws Exception
    {
        final Bundle bundle = newBundle( 0, "foo.bar" );
        final ServiceReference ref1 = newServiceReference( bundle, 0, "foo.bar.iface1" );
        final ServiceReference ref2 = newServiceReference( bundle, 0, "foo.bar.iface2", "foo.bar.iface3" );

        final BundleContext context = Mockito.mock( BundleContext.class );
        Mockito.when( context.getAllServiceReferences( null, null ) ).thenReturn( new ServiceReference[]{ref1, ref2} );

        return new OsgiServiceReporter( context );
    }

    private ServiceReference newServiceReference( final Bundle bundle, final long id, final String... ifaces )
    {
        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( ref.getBundle() ).thenReturn( bundle );
        Mockito.when( ref.getProperty( Constants.OBJECTCLASS ) ).thenReturn( ifaces );
        Mockito.when( ref.getProperty( Constants.SERVICE_ID ) ).thenReturn( id );
        return ref;
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
