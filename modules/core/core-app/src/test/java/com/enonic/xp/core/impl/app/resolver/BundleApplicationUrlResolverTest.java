package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.app.BundleBasedTest;
import com.enonic.xp.resource.Resource;

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
        builder.addResource( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

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
        builder.addResource( "dummy.txt", getClass().getResource( "/myapp/dummy.txt" ) );

        final Bundle bundle = deploy( "bundle", builder );
        this.resolver = new BundleApplicationUrlResolver( bundle );

        final Resource resource1 = this.resolver.findResource( "/site/site.xml" );
        assertNotNull( resource1 );

        final Resource resource2 = this.resolver.findResource( "site/site.xml" );
        assertNotNull( resource2 );

        final Resource resource3 = this.resolver.findResource( "site/not-found.txt" );
        assertNull( resource3 );
    }
}
