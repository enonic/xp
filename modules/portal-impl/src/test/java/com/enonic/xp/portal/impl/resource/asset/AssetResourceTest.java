package com.enonic.xp.portal.impl.resource.asset;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.impl.resource.base.ModuleBaseResourceTest;

import static org.junit.Assert.*;

public class AssetResourceTest
    extends ModuleBaseResourceTest
{
    @Override
    protected void configure()
        throws Exception
    {
        configureApplicationService();
    }

    @Test
    public void getAssetResource_found()
        throws Exception
    {
        addResource( "main.css", "demo:/site/assets/css/main.css", "p {color:red;}" );

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/asset/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/css", response.getContentType() );
        assertEquals( "p {color:red;}", response.getContentAsString() );
    }

    @Test
    public void getAssetResource_fileNotFound()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/asset/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
        assertTrue( response.getContentAsString().contains( "Module [demo] or file [css/main.css] in it not found" ) );
    }

    @Test
    public void methodNotAllowed()
        throws Exception
    {
        final MockHttpServletRequest request = newPostRequest( "/master/path/to/content/_/asset/demo/css/main.css" );
        final MockHttpServletResponse response = executeRequest( request );
        assertEquals( 405, response.getStatus() );
    }
}
