package com.enonic.wem.core.resource;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.Resource2NotFoundException;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.core.config.SystemConfig;

import static org.junit.Assert.*;

public class ResourceServiceImpl_moduleTest
    extends AbstractResourceServiceImplTest
{
    private ResourceServiceImpl resourceService;

    @Before
    public void setup()
        throws Exception
    {
        final File modulesDir = this.temporaryFolder.newFolder( "modules" );

        final SystemConfig config = Mockito.mock( SystemConfig.class );
        Mockito.when( config.getModulesDir() ).thenReturn( modulesDir.toPath() );

        this.resourceService = new ResourceServiceImpl( config );

        writeFile( modulesDir, "mymodule-1.0.0/a/b.txt", "a/b.txt" );
        writeFile( modulesDir, "mymodule-1.0.0/a/c.txt", "a/c.txt" );
        writeFile( modulesDir, "mymodule-1.0.0/a/c/d.txt", "a/c/d.txt" );
        writeFile( modulesDir, "othermodule-1.0.0/a.txt", "a.txt" );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final ResourceKey key = ResourceKey.from( "mymodule-1.0.0:/a/b.txt" );
        assertTrue( this.resourceService.hasResource( key ) );

        final Resource2 resource = this.resourceService.getResource( key );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( 7, resource.getSize() );
        assertNotNull( resource.getByteSource() );
        assertEquals( "a/b.txt", resource.getAsString() );
        assertTrue( resource.getTimestamp() > 0 );
    }

    @Test
    public void testGetChildren()
    {
        final ResourceKeys keys1 = this.resourceService.getChildren( ResourceKey.from( "mymodule-1.0.0:/not/found" ) );
        assertNotNull( keys1 );
        assertTrue( keys1.isEmpty() );

        final ResourceKeys keys2 = this.resourceService.getChildren( ResourceKey.from( "mymodule-1.0.0:/a" ) );
        assertNotNull( keys2 );
        assertFalse( keys2.isEmpty() );
        assertEquals( 3, keys2.getSize() );
        assertTrue( keys2.contains( ResourceKey.from( "mymodule-1.0.0:/a/c" ) ) );
    }

    @Test(expected = Resource2NotFoundException.class)
    public void testGetResource_notFound()
    {
        final ResourceKey key1 = ResourceKey.from( "mymodule-1.0.0:/a" );
        assertFalse( this.resourceService.hasResource( key1 ) );

        final ResourceKey key2 = ResourceKey.from( "mymodule-1.0.0:/not/exists.txt" );
        assertFalse( this.resourceService.hasResource( key2 ) );

        this.resourceService.getResource( key2 );
    }
}
