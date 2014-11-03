package com.enonic.wem.portal.internal.underscore;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.wem.portal.internal.base.ModuleBaseResourceTest;

import static org.junit.Assert.*;

public class PublicResourceTest
    extends ModuleBaseResourceTest
{
    @Override
    protected void configure()
        throws Exception
    {
        configureModuleService();

        final PublicResourceProvider provider = new PublicResourceProvider();
        provider.setModuleService( this.moduleService );

        this.factories.add( provider );
    }

    @Test
    public void getPublicResource_found()
        throws Exception
    {
        addResource( "main.css", "demo:/public/css/main.css", "p {color:red;}" );

        final MockHttpServletRequest request = newGetRequest( "/live/path/to/content/_/public/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/css", response.getContentType() );
        assertEquals( "p {color:red;}", response.getContentAsString() );
    }

    @Test
    public void getPublicResource_moduleNotFound()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/live/path/to/content/_/public/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
        assertTrue( response.getContentAsString().contains( "Module [demo] not found" ) );
    }

    @Test
    public void getPublicResource_fileNotFound()
        throws Exception
    {
        addModule( "demo" );

        final MockHttpServletRequest request = newGetRequest( "/live/path/to/content/_/public/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
        assertTrue( response.getContentAsString().contains( "File [css/main.css] not found in module [demo]" ) );
    }

    @Test
    public void methodNotAllowed()
        throws Exception
    {
        final MockHttpServletRequest request = newPostRequest( "/live/path/to/content/_/public/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );
        assertEquals( 405, response.getStatus() );
    }
}
