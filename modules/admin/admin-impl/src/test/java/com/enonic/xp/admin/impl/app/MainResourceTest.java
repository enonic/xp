package com.enonic.xp.admin.impl.app;

import java.io.File;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.jaxrs.impl.MockRestResponse;

public class MainResourceTest
    extends JaxRsResourceTestSupport
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ResourceLocator locator;

    @Override
    protected Object getResourceInstance()
    {
        this.locator = Mockito.mock( ResourceLocator.class );

        final MainResource resource = new MainResource();
        resource.setResourceLocator( this.locator );
        return resource;
    }

    @Test
    public void testRedirectRoot()
        throws Exception
    {
        assertRedirect( request().path( "/" ).get() );
    }

    @Test
    public void testRedirectAdmin()
        throws Exception
    {
        assertRedirect( request().path( "/admin" ).get() );
    }

    private URL writeFile( final String text )
        throws Exception
    {
        final File file = this.folder.newFile();
        Files.write( text, file, Charsets.UTF_8 );
        return file.toURI().toURL();
    }

    @Test
    public void testResource()
        throws Exception
    {
        final URL url = writeFile( "Hello World!" );
        Mockito.when( this.locator.findResource( "/web/test.txt" ) ).thenReturn( url );

        final MockRestResponse res = request().path( "/test.txt" ).get();
        assertEquals( 200, res.getStatus() );
        assertEquals( "Hello World!", res.getDataAsString() );
        assertEquals( "text/plain", res.getHeader( "Content-Type" ) );
    }

    @Test
    public void testVersionedResource()
        throws Exception
    {
        final URL url = writeFile( "Hello World!" );
        Mockito.when( this.locator.findResource( "/web/admin/test.txt" ) ).thenReturn( url );

        final MockRestResponse res = request().path( "/admin/assets/1234/test.txt" ).get();
        assertEquals( 200, res.getStatus() );
        assertEquals( "Hello World!", res.getDataAsString() );
        assertEquals( "text/plain", res.getHeader( "Content-Type" ) );
    }

    @Test
    public void testResource_notFound()
        throws Exception
    {
        final MockRestResponse res = request().path( "/test.txt" ).get();
        assertEquals( 404, res.getStatus() );
    }

    private void assertRedirect( final MockRestResponse res )
    {
        assertEquals( 307, res.getStatus() );
        assertEquals( "admin/tool", res.getHeader( "Location" ) );
    }
}
