package com.enonic.xp.server.impl.status;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.*;

public class OsgiBundleReporterTest
    extends BaseOsgiReporterTest
{
    private OsgiBundleReporter reporter;

    @Override
    protected void initialize()
        throws Exception
    {
        final Bundle bundle1 = newBundle( 0, "foo.bar1" );
        final Bundle bundle2 = newBundle( 1, "foo.bar2" );

        final BundleContext context = Mockito.mock( BundleContext.class );
        Mockito.when( context.getBundles() ).thenReturn( new Bundle[]{bundle1, bundle2} );

        this.reporter = new OsgiBundleReporter();
        this.reporter.activate( context );
    }

    @Test
    public void testName()
    {
        assertEquals( "osgi.bundle", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }

    @Test
    public void testStateString()
    {
        assertEquals( "ACTIVE", this.reporter.stateAsString( Bundle.ACTIVE ) );
        assertEquals( "STARTING", this.reporter.stateAsString( Bundle.STARTING ) );
        assertEquals( "STOPPING", this.reporter.stateAsString( Bundle.STOPPING ) );
        assertEquals( "RESOLVED", this.reporter.stateAsString( Bundle.RESOLVED ) );
        assertEquals( "INSTALLED", this.reporter.stateAsString( Bundle.INSTALLED ) );
        assertEquals( "UNINSTALLED", this.reporter.stateAsString( Bundle.UNINSTALLED ) );
        assertEquals( "UNKNOWN", this.reporter.stateAsString( Integer.MAX_VALUE ) );
    }
}
