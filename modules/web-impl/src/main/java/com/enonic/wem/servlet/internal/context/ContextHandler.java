package com.enonic.wem.servlet.internal.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;

@Component(immediate = true)
public final class ContextHandler
    implements WebHandler
{
    @Override
    public int getOrder()
    {
        return MIN_ORDER;
    }

    @Override
    public boolean handle( final WebContext context )
        throws Exception
    {
        final Context current = ContextAccessor.current();

        final HttpServletRequest req = context.getRequest();
        final HttpSession session = req.getSession( true );

        current.getLocalScope().setSession( new SessionWrapper( session ) );
        context.setRequest( new HttpRequestDelegate( req ) );

        return false;
    }
}
