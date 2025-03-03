package com.enonic.xp.web.impl.handler;

import java.util.Comparator;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;

@Component
public final class WebDispatcherImpl
    implements WebDispatcher
{
    private final AtomicSortedList<WebHandler> webHandlerList = new AtomicSortedList<>( Comparator.comparingInt( WebHandler::getOrder ) );

    @Override
    public void add( final WebHandler webHandler )
    {
        webHandlerList.add( webHandler );
    }

    @Override
    public void remove( final WebHandler webHandler )
    {
        webHandlerList.remove( webHandler );
    }

    @Override
    public WebResponse dispatch( final WebRequest req, final WebResponse res )
        throws Exception
    {
        return new WebHandlerChainImpl( this.webHandlerList.snapshot() ).handle( req, res );
    }

    @Override
    public List<WebHandler> list()
    {
        return this.webHandlerList.snapshot();
    }
}
