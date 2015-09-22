package com.enonic.xp.resource;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileResourceTest
    extends AbstractResourceTest
{
    @Test
    public void testGetResource()
        throws Exception
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/a/b.txt" );
        final File resourceFile = new File( applicationsDir, "myapplication/a/b.txt" );

        final Resource resource = new FileResource( key, resourceFile );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( 7, resource.getSize() );
        assertTrue( resource.getTimestamp() > 0 );
        assertTrue( resource.exists() );
        assertEquals( resourceFile.toURI().toURL(), resource.getUrl() );

        resource.requireExists();
        assertNotNull( resource.openStream() );
        assertNotNull( resource.readBytes() );
        assertEquals( "a/b.txt", resource.readString() );
        assertEquals( "a/b.txt", resource.readLines().get( 0 ) );
    }

    @Test
    public void testGetResource_notFound()
        throws MalformedURLException
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/not/exists.txt" );
        final File resourceFile = new File( applicationsDir, "myapplication/not/exists.txt" );

        final Resource resource = new FileResource( key, resourceFile );
        assertNotNull( resource );
        assertEquals( key, resource.getKey() );
        assertEquals( -1, resource.getSize() );
        assertEquals( -1, resource.getTimestamp() );
        assertFalse( resource.exists() );
        assertEquals( resourceFile.toURI().toURL(), resource.getUrl() );

        boolean requireExistExceptionCaught = false;
        try
        {
            resource.requireExists();
        }
        catch ( ResourceNotFoundException e )
        {
            requireExistExceptionCaught = true;
        }
        assertTrue( requireExistExceptionCaught );

        boolean openStreamExceptionCaught = false;
        try
        {
            resource.openStream();
        }
        catch ( ResourceNotFoundException e )
        {
            openStreamExceptionCaught = true;
        }
        assertTrue( openStreamExceptionCaught );

        boolean readBytesExceptionCaught = false;
        try
        {
            resource.readBytes();
        }
        catch ( ResourceNotFoundException e )
        {
            readBytesExceptionCaught = true;
        }
        assertTrue( readBytesExceptionCaught );

        boolean readStringExceptionCaught = false;
        try
        {
            resource.readString();
        }
        catch ( ResourceNotFoundException e )
        {
            readStringExceptionCaught = true;
        }
        assertTrue( readStringExceptionCaught );

        boolean readLinesExceptionCaught = false;
        try
        {
            resource.readLines();
        }
        catch ( ResourceNotFoundException e )
        {
            readLinesExceptionCaught = true;
        }
        assertTrue( readLinesExceptionCaught );

    }
}
