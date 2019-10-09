package com.enonic.xp.web.impl.handler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component
public final class WebDispatcherImpl
    implements WebDispatcher
{
    private final NavigableSet<WebHandler> webHandlerList = new ConcurrentSkipListSet<>( Comparator.comparingInt( WebHandler::getOrder ) );

    @Override
    public void add( final WebHandler webHandler )
    {
        this.webHandlerList.add( webHandler );
    }

    @Override
    public void remove( final WebHandler webHandler )
    {
        this.webHandlerList.remove( webHandler );
    }

    @Override
    public WebResponse dispatch( final WebRequest req, final WebResponse res )
        throws Exception
    {
        ServletRequestHolder.setRequest( req.getRawRequest() );
        return new WebHandlerChainImpl( this.webHandlerList ).handle( req, res );
    }

    @Override
    public Iterator<WebHandler> iterator()
    {
        return this.webHandlerList.iterator();
    }
}
