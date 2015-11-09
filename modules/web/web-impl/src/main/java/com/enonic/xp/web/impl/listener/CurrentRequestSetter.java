package com.enonic.xp.web.impl.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, property = "osgi.http.whiteboard.listener=true")
public final class CurrentRequestSetter
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
