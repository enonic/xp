package com.enonic.xp.core.impl.app.resolver;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.junit.Assert.*;

public class BundleApplicationUrlResolverTest
    extends BundleBasedTest
{
    private BundleApplicationUrlResolver resolver;

    @Before
    public void initResolver()
        throws Exception
    {
        final InputStream in = newBundle( "foo.bar.bundle" ).
            add( "site/site.xml", getClass().getResource( "/bundles/bundle1/site/site.xml" ) ).
            add( "dummy.txt", getClass().getResource( "/bundles/bundle1/dummy.txt" ) ).
            build();

        final Bundle bundle = deploy( "bundle", in );
        this.resolver = new BundleApplicationUrlResolver( bundle );
    }

    @Test
    public void testFindFiles()
    {
        final Set<String> files = this.resolver.findFiles();
        assertEquals( 3, files.size() );
        assertTrue( files.contains( "site/site.xml" ) );
        assertTrue( files.contains( "dummy.txt" ) );
    }

    @Test
    public void testFindUrl()
    {
        final URL url1 = this.resolver.findUrl( "/site/site.xml" );
        assertNotNull( url1 );

        final URL url2 = this.resolver.findUrl( "site/site.xml" );
        assertNotNull( url2 );

        final URL url3 = this.resolver.findUrl( "site/not-found.txt" );
        assertNull( url3 );
    }
}
