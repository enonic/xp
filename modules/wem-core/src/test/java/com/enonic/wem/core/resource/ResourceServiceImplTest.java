package com.enonic.wem.core.resource;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.config.SystemConfig;

import static org.junit.Assert.*;

public class ResourceServiceImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ResourceServiceImpl resourceService;

    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

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
        final ModuleResourceKey key = ModuleResourceKey.from( "mymodule-1.0.0:/a/b.txt" );
        assertTrue( this.resourceService.hasResource( key ) );

        final Resource resource = this.resourceService.getResource( key );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( 7, resource.getSize() );
        assertNotNull( resource.getByteSource() );
        assertEquals( "a/b.txt", resource.readAsString() );
        assertEquals( "a/b.txt", resource.readLines().get( 0 ) );
        assertTrue( resource.getTimestamp() > 0 );
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetResource_notFound()
    {
        final ModuleResourceKey key1 = ModuleResourceKey.from( "mymodule-1.0.0:/a" );
        assertFalse( this.resourceService.hasResource( key1 ) );

        final ModuleResourceKey key2 = ModuleResourceKey.from( "mymodule-1.0.0:/not/exists.txt" );
        assertFalse( this.resourceService.hasResource( key2 ) );

        this.resourceService.getResource( key2 );
    }
}
