package com.enonic.xp.admin.impl.app;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.Assert.*;

public class MainWebHandlerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ResourceLocator locator;

    private MainWebHandler handler;

    @Before
    public void setup()
    {
        this.locator = Mockito.mock( ResourceLocator.class );
        this.handler = new MainWebHandler();
        this.handler.setResourceLocator( this.locator );
    }

    @Test
    public void testRedirect()
        throws Exception
    {
        final WebRequest request = new WebRequest();

        request.setPath( "/other" );
        assertFalse( this.handler.canHandle( request ) );

        request.setPath( "/" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response1 = this.handler.doHandle( request, null, null );
        assertRedirect( response1 );

        request.setPath( "/admin" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response2 = this.handler.doHandle( request, null, null );
        assertRedirect( response2 );
    }

    private URL writeFile( final String text )
        throws Exception
    {
        final File file = this.folder.newFile();
        Files.write( text, file, Charsets.UTF_8 );
        return file.toURI().toURL();
    }

    @Test
    public void testVersionedResource()
        throws Exception
    {
        final WebRequest request = new WebRequest();

        request.setPath( "/admin/assets/1234/test.txt" );
        assertTrue( this.handler.canHandle( request ) );

        final URL url = writeFile( "Hello World!" );
        Mockito.when( this.locator.findResource( "/web/admin/test.txt" ) ).thenReturn( url );

        final WebResponse response = this.handler.doHandle( request, null, null );
        assertEquals( 200, response.getStatus().value() );
        assertEquals( "Hello World!", new String( (byte[]) response.getBody() ) );
        assertEquals( "text/plain", response.getContentType().toString() );
    }

    @Test
    public void testResource_notFound()
        throws Exception
    {
        final WebRequest request = new WebRequest();

        request.setPath( "/admin/assets/1234/test.txt" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response = this.handler.doHandle( request, null, null );
        assertEquals( 404, response.getStatus().value() );
    }

    private void assertRedirect( final WebResponse res )
    {
        assertEquals( 307, res.getStatus().value() );
        assertEquals( "/admin/tool", res.getHeaders().get( "Location" ) );
    }
}
