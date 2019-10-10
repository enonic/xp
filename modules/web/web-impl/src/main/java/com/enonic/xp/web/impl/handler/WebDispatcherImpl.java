package com.enonic.xp.web.impl.handler;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component
public final class WebDispatcherImpl
    implements WebDispatcher
{
    private final List<WebHandler> webHandlerList = Lists.newCopyOnWriteArrayList();

    @Override
    public synchronized void add( final WebHandler webHandler )
    {
        this.webHandlerList.add( webHandler );
        sortWebHandlerList();
    }

    @Override
    public synchronized void remove( final WebHandler webHandler )
    {
        this.webHandlerList.remove( webHandler );
        sortWebHandlerList();
    }

    private void sortWebHandlerList()
    {
        this.webHandlerList.sort( this::compare );
    }

    private int compare( final WebHandler webHandler1, final WebHandler webHandler2 )
    {
        return webHandler1.getOrder() - webHandler2.getOrder();
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
