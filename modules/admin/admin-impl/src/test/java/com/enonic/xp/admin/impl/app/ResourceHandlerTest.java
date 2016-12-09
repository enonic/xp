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

import com.enonic.xp.web.WebResponse;

import static org.junit.Assert.*;

public class ResourceHandlerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ResourceHandler handler;

    private ResourceLocator locator;

    @Before
    public void setup()
    {
        this.locator = Mockito.mock( ResourceLocator.class );
        this.handler = new ResourceHandler();
        this.handler.setResourceLocator( this.locator );
    }

    private URL writeFile( final String text )
        throws Exception
    {
        final File file = this.folder.newFile();
        Files.write( text, file, Charsets.UTF_8 );
        return file.toURI().toURL();
    }

    @Test
    public void handle()
        throws Exception
    {
        final URL url = writeFile( "Hello World!" );
        Mockito.when( this.locator.findResource( "/web/test.txt" ) ).thenReturn( url );

        final WebResponse res = this.handler.handle( "/test.txt" );
        assertEquals( 200, res.getStatus().value() );
        assertEquals( "text/plain", res.getContentType().toString() );
    }

    @Test
    public void handle_notFound()
        throws Exception
    {
        final WebResponse res = this.handler.handle( "/test.txt" );
        assertEquals( 404, res.getStatus().value() );
    }

    @Test
    public void handle_cached()
        throws Exception
    {
        final URL url = writeFile( "Hello World!" );
        Mockito.when( this.locator.findResource( "/web/test.txt" ) ).thenReturn( url );
        Mockito.when( this.locator.shouldCache() ).thenReturn( true );

        final WebResponse res = this.handler.handle( "/test.txt", true );
        assertEquals( 200, res.getStatus().value() );
        assertEquals( "text/plain", res.getContentType().toString() );
        assertEquals( "public, no-transform, max-age=31536000", res.getHeaders().get( "Cache-Control" ) );
    }
}
