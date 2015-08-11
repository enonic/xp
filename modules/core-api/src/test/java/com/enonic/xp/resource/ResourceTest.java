package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import static org.junit.Assert.*;

public class ResourceTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File applicationsDir;

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
        applicationsDir = this.temporaryFolder.newFolder( "applications" );

        writeFile( applicationsDir, "myapplication/a/b.txt", "a/b.txt" );
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/a/b.txt" );

        final Resource resource = new Resource( key, new File( applicationsDir, "myapplication/a/b.txt" ).toURI().toURL() );
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
        throws MalformedURLException
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/not/exists.txt" );

        final Resource resource = new Resource( key, new File( applicationsDir, "myapplication/not/exists.txt" ).toURI().toURL() );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( -1, resource.getSize() );
        assertEquals( -1, resource.getTimestamp() );
        assertFalse( resource.exists() );
    }
}
