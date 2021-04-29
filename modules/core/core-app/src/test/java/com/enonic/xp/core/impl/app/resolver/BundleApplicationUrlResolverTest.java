package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BundleApplicationUrlResolverTest
    extends BundleBasedTest
{
    private BundleApplicationUrlResolver resolver;

    @Test
    public void testFindFiles()
        throws Exception
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", true );
        builder.add( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
        this.resolver = new BundleApplicationUrlResolver( bundle );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 3, files.size() );
        assertTrue( files.contains( "site/site.xml" ) );
        assertTrue( files.contains( "dummy.txt" ) );
    }

    @Test
    public void testFindUrl()
    {
        final TinyBundle builder = newBundle( "foo.bar.bundle", true );
        builder.add( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
        this.resolver = new BundleApplicationUrlResolver( bundle );

        final URL url1 = this.resolver.findUrl( "/site/site.xml" );
        assertNotNull( url1 );

        final URL url2 = this.resolver.findUrl( "site/site.xml" );
        assertNotNull( url2 );

        final URL url3 = this.resolver.findUrl( "site/not-found.txt" );
        assertNull( url3 );
    }
}
