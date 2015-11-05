package com.enonic.xp.web.impl.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class CurrentRequestSetterTest
{
    private CurrentRequestSetter listener;

    private ServletContext context;

    private HttpServletRequest request;


    @Before
    public void setup()
    {
        this.listener = new CurrentRequestSetter();
        this.context = Mockito.mock( ServletContext.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( null );
    }

    @Test
    public void testLifecycle()
    {
        final ServletRequestEvent event = new ServletRequestEvent( this.context, request );

        assertNull( ServletRequestHolder.getRequest() );
        this.listener.requestInitialized( event );
        assertSame( this.request, ServletRequestHolder.getRequest() );

        this.listener.requestDestroyed( event );
        assertNull( ServletRequestHolder.getRequest() );
    }
}
