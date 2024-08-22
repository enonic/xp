package com.enonic.xp.admin.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        request.setRawPath( "/other" );
        assertFalse( this.handler.canHandle( request ) );

        request.setRawPath( "/" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response1 = this.handler.doHandle( request, null, null );
        assertRedirect( response1 );

        request.setRawPath( "/admin" );
        assertTrue( this.handler.canHandle( request ) );

        final WebResponse response2 = this.handler.doHandle( request, null, null );
        assertRedirect( response2 );
    }

    private void assertRedirect( final WebResponse res )
    {
        assertEquals( 307, res.getStatus().value() );
        assertEquals( "/admin/tool", res.getHeaders().get( "Location" ) );
    }
}
