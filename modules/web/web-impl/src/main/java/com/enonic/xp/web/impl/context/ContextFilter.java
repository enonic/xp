package com.enonic.xp.web.impl.context;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, service = Filter.class,
    property = {"osgi.http.whiteboard.filter.pattern=/", "service.ranking:Integer=10"})
public final class ContextFilter
    extends OncePerRequestFilter
{
    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        final Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( ContentConstants.BRANCH_DRAFT );
        context.getLocalScope().setAttribute( ContentConstants.CONTENT_REPO.getId() );

        final HttpSession session = req.getSession( true );
        context.getLocalScope().setSession( new SessionWrapper( session ) );

        context.callWith( () -> {
            chain.doFilter( wrapRequest( req ), res );
            return null;
        } );
    }

    private HttpServletRequest wrapRequest( final HttpServletRequest req )
    {
        final HttpServletRequest wrapped = new HttpRequestDelegate( req );
        ServletRequestHolder.setRequest( wrapped );
        return wrapped;
    }
}
