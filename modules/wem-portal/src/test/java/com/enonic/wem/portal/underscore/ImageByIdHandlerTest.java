package com.enonic.wem.portal.underscore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

import static org.junit.Assert.*;

public class ImageByIdHandlerTest
    extends ImageBaseHandlerTest<ImageByIdHandler>
{
    @Override
    protected ImageByIdHandler createResource()
    {
        return new ImageByIdHandler();
    }

    @Before
    public void setup()
        throws Exception
    {
        super.setup();
    }

    @Test
    public void getImageFound()
        throws Exception
    {
        setupContent();

        final ClientResponse response = executeGet( "/live/path/to/content/_/image/id/content-id" );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getType().toString() );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getById( Mockito.anyObject(), Mockito.anyObject() ) ).thenReturn( null );

        final ClientResponse response = executeGet( "/live/path/to/content/_/image/id/content-id" );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getImageWithFilter()
        throws Exception
    {
        setupContent();

        final ClientResponse response =
            executeGet( "/live/path/to/content/_/image/id/content-id?filter=sepia()&quality=75&background=0x0" );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getType().toString() );
    }
}
