package com.enonic.xp.server.impl.status;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class OsgiServiceReporterTest
    extends BaseOsgiReporterTest
{
    private OsgiServiceReporter reporter;

    @Override
    protected void initialize()
        throws Exception
    {
        final Bundle bundle = newBundle( 0, "foo.bar" );
        final ServiceReference ref1 = newServiceReference( bundle, 0, "foo.bar.iface1" );
        final ServiceReference ref2 = newServiceReference( bundle, 0, "foo.bar.iface2", "foo.bar.iface3" );

        final BundleContext context = Mockito.mock( BundleContext.class );
        Mockito.when( context.getAllServiceReferences( null, null ) ).thenReturn( new ServiceReference[]{ref1, ref2} );

        this.reporter = new OsgiServiceReporter();
        this.reporter.activate( context );
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
    public void testName()
    {
        assertEquals( "osgi.service", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
