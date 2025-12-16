package com.enonic.xp.web.impl.header;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.ee11.servlets.HeaderFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.filter.FilterConfigInitParametersOverride;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, configurationPid = "com.enonic.xp.web.header", property = {"connector=xp",
    "connector=api", "connector=status"})
@Order(-500)
@WebFilter("/*")
public class HeaderFilterWrapper
    extends OncePerRequestFilter
{
    private final String headerConfig;

    private Filter delegate;

    @Activate
    public HeaderFilterWrapper( HeaderFilterConfig headerFilterConfig )
    {
        this.headerConfig = headerFilterConfig.headerConfig();
    }

    @Override
    public void init( final FilterConfig filterConfig )
        throws ServletException
    {
        delegate = new HeaderFilter();
        delegate.init( new FilterConfigInitParametersOverride( filterConfig, Map.of( "headerConfig", headerConfig ) ) );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws IOException, ServletException
    {
        delegate.doFilter( req, res, chain );
    }

    @Override
    public void destroy()
    {
        delegate.destroy();
    }
}
