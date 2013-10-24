package com.enonic.wem.core.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class RequestContextListenerTest
{
    @Test
    public void testInitDestroy()
    {
        final ServletContext context = Mockito.mock( ServletContext.class );
        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        final ServletRequestEvent event = new ServletRequestEvent( context, request );
        final RequestContextListener listener = new RequestContextListener();

        assertNoRequestBound();
        listener.requestInitialized( event );
        assertSame( request, ServletRequestHolder.getRequest() );

        listener.requestDestroyed( event );
        assertNoRequestBound();
    }

    private void assertNoRequestBound()
    {
        try
        {
            ServletRequestHolder.getRequest();
            fail( "Should throw exception" );
        }
        catch ( final IllegalStateException e )
        {
            assertEquals( "No request bound to thread", e.getMessage() );
        }
    }
}
