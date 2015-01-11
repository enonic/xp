package com.enonic.xp.web.impl.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.OncePerRequestHandler;

@Component(immediate = true, service = WebHandler.class)
public final class ContextHandler
    extends OncePerRequestHandler
{
    public ContextHandler()
    {
        setOrder( MIN_ORDER );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return true;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Context context = ContextAccessor.current();
        final HttpSession session = req.getSession( true );

        context.getLocalScope().setSession( new SessionWrapper( session ) );
        chain.handle( new HttpRequestDelegate( req ), res );
    }
}
