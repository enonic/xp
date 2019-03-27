package com.enonic.xp.web.impl.context;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api", "connector=status"})
@Order(-180)
@WebFilter("/*")
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
        context.getLocalScope().setSession( new SessionWrapper( req ) );

        context.callWith( () -> {
            chain.doFilter( new HttpRequestDelegate( req ), res );
            return null;
        } );
    }
}
