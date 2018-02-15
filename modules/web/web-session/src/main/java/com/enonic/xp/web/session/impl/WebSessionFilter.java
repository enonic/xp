package com.enonic.xp.web.session.impl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ignite.Ignite;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.filter.OncePerRequestFilter;

import static org.apache.ignite.cache.websession.WebSessionFilter.WEB_SES_CACHE_NAME_PARAM;
import static org.apache.ignite.cache.websession.WebSessionFilter.WEB_SES_KEEP_BINARY_PARAM;
import static org.apache.ignite.cache.websession.WebSessionFilter.WEB_SES_NAME_PARAM;

@Component(immediate = true, service = Filter.class, configurationPid = "com.enonic.xp.web.session")
@Order(-190)
@WebFilter("/*")
public class WebSessionFilter
    extends OncePerRequestFilter
{
    public static final String WEB_SESSION_CACHE = "com.enonic.xp.webSessionCache";

    private WebSessionConfig config;

    private Ignite ignite;

    private org.apache.ignite.cache.websession.WebSessionFilter webSessionFilter;

    @SuppressWarnings("unused")
    @Activate
    public void activate( final WebSessionConfig config )
        throws Exception
    {
        this.config = config;
    }

    @Override
    public void init( final FilterConfig config )
        throws ServletException
    {
        super.init( config );

        this.ignite.getOrCreateCache( SessionCacheConfigFactory.create( WEB_SESSION_CACHE, this.config ) );

        final FilterConfigImpl mergedConfig = new FilterConfigImpl( config );
        mergedConfig.populate( this.config );
        mergedConfig.populate( WEB_SES_CACHE_NAME_PARAM, WEB_SESSION_CACHE );
        mergedConfig.populate( WEB_SES_NAME_PARAM, this.ignite.name() );
        // This cannot be true atm since the classloader will fail to find the class
        // because the WebSessionV2 uses context classloader to fetch class instead
        // of the Ignite configured classloader.
        mergedConfig.populate( WEB_SES_KEEP_BINARY_PARAM, "false" );

        this.webSessionFilter = new org.apache.ignite.cache.websession.WebSessionFilter();
        this.webSessionFilter.init( mergedConfig );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        this.webSessionFilter.doFilter( req, res, chain );
    }

    @SuppressWarnings("unused")
    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }
}