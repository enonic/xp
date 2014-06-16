package com.enonic.wem.portal.underscore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

import static org.junit.Assert.*;

public class ImageByNameHandlerTest
    extends ImageBaseHandlerTest<ImageByNameHandler>
{
    @Override
    protected ImageByNameHandler createResource()
    {
        return new ImageByNameHandler();
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

        final ClientResponse response = executeGet( "/live/path/to/content/_/image/enonic-logo.png" );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getType().toString() );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject(), Mockito.anyObject() ) ).thenReturn( null );

        final ClientResponse response = executeGet( "/live/path/to/content/_/image/enonic-logo.png" );
        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getImageWithFilter()
        throws Exception
    {
        setupContent();

        final ClientResponse response =
            executeGet( "/live/path/to/content/_/image/enonic-logo.png?filter=sepia()&quality=75&background=0x0" );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getType().toString() );
    }
}