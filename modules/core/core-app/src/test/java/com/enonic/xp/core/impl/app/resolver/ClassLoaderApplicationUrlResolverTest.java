package com.enonic.xp.core.impl.app.resolver;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassLoaderApplicationUrlResolverTest
{
    private ClassLoaderApplicationUrlResolver resolver;

    @BeforeEach
    public void setup()
        throws Exception
    {
        URL[] resourcesPath = {new File( "src/test/resources" ).toURI().toURL()};
        URLClassLoader loader = new URLClassLoader( resourcesPath, ClassLoader.getSystemClassLoader() );
        this.resolver = new ClassLoaderApplicationUrlResolver( loader );
    }

    @Test
    public void testFindFiles()
    {
        final Set<String> files = this.resolver.findFiles();
        assertFalse( files.isEmpty() );
        assertTrue( files.contains( "myapp/site/site.xml" ) );
    }

    @Test
    public void testFindUrl()
    {
        final URL url1 = this.resolver.findUrl( "/myapp/site/site.xml" );
        assertNotNull( url1 );

        final URL url2 = this.resolver.findUrl( "myapp/site/site.xml" );
        assertNotNull( url2 );

        final URL url3 = this.resolver.findUrl( "myapp/not-found.txt" );
        assertNull( url3 );
    }
}
