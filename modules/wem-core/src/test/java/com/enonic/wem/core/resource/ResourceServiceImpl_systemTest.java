package com.enonic.wem.core.resource;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceKey;

import static org.junit.Assert.*;

public class ResourceServiceImpl_systemTest
    extends AbstractResourceServiceImplTest
{
    private ResourceServiceImpl resourceService;

    @Before
    public void setup()
        throws Exception
    {
        final File workDir = this.temporaryFolder.newFolder( "work" );
        final ClassLoader classLoader = new URLClassLoader( new URL[]{workDir.toURI().toURL()}, null );

        this.resourceService = new ResourceServiceImpl( null, classLoader );

        writeFile( workDir, "system/a/b.txt", "a/b.txt" );
        writeFile( workDir, "system/a/c.txt", "a/c.txt" );
        writeFile( workDir, "system/a/c/d.txt", "a/c/d.txt" );
        writeFile( workDir, "system/a.txt", "a.txt" );
    }

    @Test
    public void testGetResource()
    {
        final ResourceKey key = ResourceKey.from( "system-0.0.0:/a/b.txt" );
        assertTrue( this.resourceService.hasResource( key ) );

        final Resource resource = this.resourceService.getResource( key );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( 7, resource.getSize() );
        assertNotNull( resource.getByteSource() );
        assertEquals( "a/b.txt", resource.getAsString() );
        assertTrue( resource.getTimestamp() > 0 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetChildren()
    {
        this.resourceService.getChildren( ResourceKey.from( "system-0.0.0:/a" ) );
    }

    @Test
    public void testGetResource_folder()
    {
        final ResourceKey key = ResourceKey.from( "system-0.0.0:/a" );
        assertTrue( this.resourceService.hasResource( key ) );

        final Resource resource = this.resourceService.getResource( key );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertTrue( resource.getTimestamp() > 0 );
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetResource_notFound()
    {
        final ResourceKey key = ResourceKey.from( "system-0.0.0:/not/exists.txt" );
        assertFalse( this.resourceService.hasResource( key ) );

        this.resourceService.getResource( key );
    }
}
