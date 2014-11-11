package com.enonic.wem.servlet.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.MutableContext;

@Component(immediate = true, service = Filter.class, property = {"urlPatterns=/*"})
public final class ContextFilter
    implements Filter
{
    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
    }

    @Override
    public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
    }

    private void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        final HttpSession httpSession = req.getSession( true );
        final MutableContext context = (MutableContext) ContextAccessor.current();
        context.setSession( new SessionWrapper( httpSession ) );
        chain.doFilter( new HttpRequestDelegate( req ), res );
    }

    @Override
    public void destroy()
    {
    }
}
