package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Hashtable;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public abstract class ApplicationBundleTest
{
    protected BundleContext bundleContext;

    @Before
    public final void setup()
        throws Exception
    {
        this.bundleContext = Mockito.mock( BundleContext.class );
        initialize();
    }

    protected abstract void initialize()
        throws Exception;

    protected final void startBundles( final Bundle... bundles )
        throws Exception
    {
        for ( final Bundle bundle : bundles )
        {
            Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        }

        Mockito.when( this.bundleContext.getBundles() ).thenReturn( bundles );
    }

    protected final Bundle newBundle( final String name, final String displayName )
        throws Exception
    {
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, name );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );
        headers.put( Constants.BUNDLE_NAME, displayName );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( name );
        Mockito.when( bundle.getEntry( Mockito.any() ) ).then( i -> doGetResource( name, i ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        return bundle;
    }

    private URL doGetResource( final String name, final InvocationOnMock invocation )
        throws Exception
    {
        return getClass().getClassLoader().getResource( "bundles/" + name + "/" + invocation.getArguments()[0].toString() );
    }
}
