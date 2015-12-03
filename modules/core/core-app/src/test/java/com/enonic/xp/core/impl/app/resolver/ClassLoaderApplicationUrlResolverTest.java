package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassLoaderApplicationUrlResolverTest
{
    private ClassLoaderApplicationUrlResolver resolver;

    @Before
    public void setup()
    {
        this.resolver = new ClassLoaderApplicationUrlResolver( getClass().getClassLoader() );
    }

    @Test
    public void testFindFiles()
    {
        final Set<String> files = this.resolver.findFiles();
        assertFalse( files.isEmpty() );
        assertTrue( files.contains( "bundles/bundle1/site/site.xml" ) );
    }

    @Test
    public void testFindUrl()
    {
        final URL url1 = this.resolver.findUrl( "/bundles/bundle1/site/site.xml" );
        assertNotNull( url1 );

        final URL url2 = this.resolver.findUrl( "bundles/bundle1/site/site.xml" );
        assertNotNull( url2 );

        final URL url3 = this.resolver.findUrl( "bundles/bundle1/site/not-found.txt" );
        assertNull( url3 );
    }
}
