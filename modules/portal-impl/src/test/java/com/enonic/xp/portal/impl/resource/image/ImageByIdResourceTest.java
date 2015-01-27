package com.enonic.xp.portal.impl.resource.image;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

public class ImageByIdResourceTest
    extends ImageBaseResourceTest
{
    @Test
    public void getImageFound()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/image/content-id/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getById( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/image/content-id/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getImageWithFilter()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/image/content-id/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }
}
