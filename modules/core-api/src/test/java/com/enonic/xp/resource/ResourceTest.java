package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ResourceTest
{

    private ResourceService resourceService;

    private ApplicationKey applicationKey;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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

        writeFile( modulesDir, "mymodule/a/b.txt", "a/b.txt" );
        writeFile( modulesDir, "mymodule/a/c.txt", "a/c.txt" );
        writeFile( modulesDir, "mymodule/a/c/d.txt", "a/c/d.txt" );
        writeFile( modulesDir, "othermodule/a.txt", "a.txt" );

        applicationKey = ApplicationKey.from( "mymodule" );
        this.resourceService = Mockito.mock( ResourceService.class );

        final ResourceUrlRegistry registry = ResourceUrlTestHelper.mockModuleScheme();
        registry.modulesDir( modulesDir );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final ResourceKey key = ResourceKey.from( applicationKey, "/a/b.txt" );

        mockResource( "mymodule:/a/b.txt" );

        final Resource resource = resourceService.getResource( key );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( 7, resource.getSize() );
        assertNotNull( resource.openStream() );
        assertNotNull( resource.readBytes() );
        assertEquals( "a/b.txt", resource.readString() );
        assertEquals( "a/b.txt", resource.readLines().get( 0 ) );
        assertTrue( resource.getTimestamp() > 0 );
        assertTrue( resource.exists() );
    }

    @Test
    public void testGetResource_notFound()
    {
        final ResourceKey key = ResourceKey.from( applicationKey, "/not/exists.txt" );

        final Resource resource = resourceService.getResource( key );
        assertNull( resource );
    }

    protected void mockResource( String uri )
        throws Exception
    {
        ResourceKey key = ResourceKey.from( uri );
        Resource res = new Resource( ResourceKey.from( uri ), new URL( "module:" + uri ) );
        Mockito.when( this.resourceService.getResource( key ) ).thenReturn( res );
    }
}
