package com.enonic.xp.portal.impl.resource.image;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.scale.ScaleParams;

import static org.junit.Assert.*;

public class ImageByIdResourceTest
    extends ImageBaseResourceTest
{
    @Test
    @Ignore
    public void getImageFound()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    @Ignore
    public void getImageNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getById( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        final MockHttpServletResponse response = executeRequest( request );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    @Ignore
    public void getImageWithFilter()
        throws Exception
    {
        setupContent();

        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    @Ignore
    public void getImageWithCache()
        throws Exception
    {
        setupContent();

        MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageScaleFunctionBuilder(), Mockito.atMost( 1 ) ).
            build( Mockito.isA( ScaleParams.class ), Mockito.isA( FocalPoint.class ) );

        //Second request using cache
        request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageScaleFunctionBuilder(), Mockito.atMost( 1 ) ).
            build( Mockito.isA( ScaleParams.class ), Mockito.isA( FocalPoint.class ) );
    }

    @Test
    @Ignore
    public void getImageWithFilterAndCache()
        throws Exception
    {
        setupContent();

        //First request
        MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageFilterBuilder(), Mockito.atMost( 1 ) ).build( Mockito.isA( String.class ) );
        Mockito.verify( this.services.getImageScaleFunctionBuilder(), Mockito.atMost( 1 ) ).
            build( Mockito.isA( ScaleParams.class ), Mockito.isA( FocalPoint.class ) );

        //Second request using cache
        request = newGetRequest( "/master/path/to/content/_/image/content-id/scale-100-100/image-name.jpg" );
        request.setQueryString( "filter=sepia()&quality=75&background=0x0" );
        response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
        Mockito.verify( this.services.getImageFilterBuilder(), Mockito.atMost( 1 ) ).build( Mockito.isA( String.class ) );
        Mockito.verify( this.services.getImageScaleFunctionBuilder(), Mockito.atMost( 1 ) ).
            build( Mockito.isA( ScaleParams.class ), Mockito.isA( FocalPoint.class ) );
    }
}
