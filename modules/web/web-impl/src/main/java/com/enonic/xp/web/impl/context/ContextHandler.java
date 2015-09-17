package com.enonic.xp.web.impl.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.handler.OncePerRequestHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

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
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( ContentConstants.BRANCH_DRAFT );
        context.getLocalScope().setAttribute( ContentConstants.CONTENT_REPO.getId() );

        final HttpSession session = req.getSession( true );
        context.getLocalScope().setSession( new SessionWrapper( session ) );

        context.callWith( () -> {
            chain.handle( new HttpRequestDelegate( req ), res );
            return null;
        } );
    }
}
