package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.junit.jupiter.api.Assertions.*;

public class BundleApplicationUrlResolverTest
    extends BundleBasedTest
{
    private BundleApplicationUrlResolver resolver;

    @BeforeEach
    public void initResolver()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", true );
        builder.add( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
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
