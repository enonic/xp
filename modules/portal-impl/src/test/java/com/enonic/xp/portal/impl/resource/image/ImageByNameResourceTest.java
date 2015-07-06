package com.enonic.xp.portal.impl.resource.image;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

public class ImageByNameResourceTest
    extends ImageBaseResourceTest
{
    @Test
    public void getImageFound()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/image-name.jpg/_/image/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/image-name.jpg/_/image/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getImageWithFilter()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/image-name.jpg/_/image/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }


    @Test
    public void getImageWithFilterAndCaching()
        throws Exception
    {
        setupContent();

        //First request
        MockHttpServletRequest request = newGetRequest( "/master/path/to/image-name.jpg/_/image/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageFilterBuilder(), Mockito.atMost( 1 ) ).build( Mockito.isA( String.class ) );

        //Second request using cache
        request = newGetRequest( "/master/path/to/image-name.jpg/_/image/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageFilterBuilder(), Mockito.atMost( 1 ) ).build( Mockito.isA( String.class ) );
    }
}
