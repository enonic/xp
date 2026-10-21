package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassLoaderApplicationUrlResolverTest
{
    @TempDir
    Path sources;

    private ClassLoaderApplicationUrlResolver resolver;

    @BeforeEach
    void setup()
        throws Exception
    {
        URL[] resourcesPath = {Path.of( "src/test/resources" ).toUri().toURL()};
        URLClassLoader loader = new URLClassLoader( resourcesPath, ClassLoader.getSystemClassLoader() );
        this.resolver = new ClassLoaderApplicationUrlResolver( loader, ApplicationKey.from( "myapp" ) );
    }

    @Test
    void testFindFiles()
    {
        final Set<String> files = this.resolver.findFiles();
        assertFalse( files.isEmpty() );
        assertTrue( files.contains( "myapp/cms/cms.yml" ) );
    }

    @Test
    void testFindUrl()
    {
        final Resource resource1 = this.resolver.findResource( "/myapp/cms/cms.yml" );
        assertNotNull( resource1 );

        final Resource resource2 = this.resolver.findResource( "myapp/cms/cms.yml" );
        assertNotNull( resource2 );

        final Resource resource3 = this.resolver.findResource( "myapp/not-found.txt" );
        assertNull( resource3 );
    }

    // a file on disk changes without any signal: the resource reports its modification time as the revision of its content
    @Test
    void file_resource_carries_the_modification_time_as_revision()
        throws Exception
    {
        final Path file = Files.writeString( Files.createDirectories( sources.resolve( "cms" ) ).resolve( "cms.yml" ), "kind: \"CMS\"" );
        final URLClassLoader loader = new URLClassLoader( new URL[]{sources.toUri().toURL()}, null );
        final ClassLoaderApplicationUrlResolver resolver = new ClassLoaderApplicationUrlResolver( loader, ApplicationKey.from( "myapp" ) );

        final Resource resource = resolver.findResource( "/cms/cms.yml" );

        assertEquals( Files.getLastModifiedTime( file ), assertInstanceOf( RevisionedResource.class, resource ).revision() );
        assertEquals( "file", resource.getResolverName() );
        assertEquals( "kind: \"CMS\"", resource.readString() );
    }
}
