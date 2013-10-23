package com.enonic.wem.core.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

public final class RequestContextListener
    implements ServletRequestListener
{
    @Override
    public void requestInitialized( final ServletRequestEvent event )
    {
        ServletRequestHolder.setRequest( (HttpServletRequest) event.getServletRequest() );
    }

    @Override
    public void requestDestroyed( final ServletRequestEvent event )
    {
        ServletRequestHolder.setRequest( null );
    }
}
