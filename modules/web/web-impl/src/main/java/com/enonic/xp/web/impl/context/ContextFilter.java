package com.enonic.xp.web.impl.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

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
        context.getLocalScope().setAttribute( "__currentTimeMillis", System.currentTimeMillis() );
        context.getLocalScope().setSession( new SessionWrapper( req ) );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost != null )
        {
            virtualHost.getContext().forEach( ( key, value ) -> {
                if ( "com.enonic.xp.repository.RepositoryId".equals( key ) )
                {
                    context.getLocalScope().setAttribute( key, RepositoryId.from( value ) );
                }
                else if ( "com.enonic.xp.branch.Branch".equals( key ) )
                {
                    context.getLocalScope().setAttribute( key, Branch.from( value ) );
                }
                else
                {
                    context.getLocalScope().setAttribute( key, value );
                }
            } );
        }

        context.callWith( () -> {
            chain.doFilter( req, res );
            return null;
        } );
    }
}
