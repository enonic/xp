package com.enonic.xp.admin.impl.app;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainWebHandlerTest
{
    private MainWebHandler handler;

    @BeforeEach
    public void setup()
    {
        this.handler = new MainWebHandler();
    }

    @Test
    public void testRedirect()
        throws Exception
    {
        final WebRequest request = new WebRequest();
        final HttpServletRequest httpServletRequest = mock( HttpServletRequest.class );

        request.setRawPath( "/" );
        request.setRawRequest( httpServletRequest );
        when( httpServletRequest.getRequestURI() ).thenReturn( "/" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response = this.handler.doHandle( request, null, null );
        assertEquals( 307, response.getStatus().value() );
        assertEquals( "/admin", response.getHeaders().get( "Location" ) );

    }

    @Test
    void cannotHandle()
    {
        final WebRequest request = new WebRequest();

        request.setRawPath( "/admin" );
        assertFalse( this.handler.canHandle( request ) );

        request.setRawPath( "/other" );
        assertFalse( this.handler.canHandle( request ) );
    }
}
