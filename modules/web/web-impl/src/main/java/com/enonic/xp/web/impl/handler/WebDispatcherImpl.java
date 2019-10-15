package com.enonic.xp.web.impl.handler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component
public final class WebDispatcherImpl
    implements WebDispatcher
{
    private final AtomicReference<ImmutableList<WebHandler>> webHandlerListRef = new AtomicReference<>( ImmutableList.of() );

    @Override
    public void add( final WebHandler webHandler )
    {
        webHandlerListRef.updateAndGet( oldWebHandlers -> Stream.concat( oldWebHandlers.stream(), Stream.of( webHandler ) ).
            sorted( Comparator.comparingInt( WebHandler::getOrder ) ).
            collect( ImmutableList.toImmutableList() ) );
    }

    @Override
    public void remove( final WebHandler webHandler )
    {
        webHandlerListRef.updateAndGet( oldWebHandlers -> oldWebHandlers.stream().filter( w -> w != webHandler ).
            sorted( Comparator.comparingInt( WebHandler::getOrder ) ).
            collect( ImmutableList.toImmutableList() ) );
    }

    @Override
    public WebResponse dispatch( final WebRequest req, final WebResponse res )
        throws Exception
    {
        ServletRequestHolder.setRequest( req.getRawRequest() );
        return new WebHandlerChainImpl( this.webHandlerListRef.get() ).handle( req, res );
    }

    @Override
    public Iterator<WebHandler> iterator()
    {
        return this.webHandlerListRef.get().iterator();
    }
}
