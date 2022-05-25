package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;

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
        URL[] resourcesPath = {Path.of( "src/test/resources" ).toUri().toURL()};
        URLClassLoader loader = new URLClassLoader( resourcesPath, ClassLoader.getSystemClassLoader() );
        this.resolver = new ClassLoaderApplicationUrlResolver( loader, ApplicationKey.from( "myapp" ) );
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
        final Resource resource1 = this.resolver.findResource( "/myapp/site/site.xml" );
        assertNotNull( resource1 );

        final Resource resource2 = this.resolver.findResource( "myapp/site/site.xml" );
        assertNotNull( resource2 );

        final Resource resource3 = this.resolver.findResource( "myapp/not-found.txt" );
        assertNull( resource3 );
    }
}
