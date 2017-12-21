package com.enonic.xp.web.vhost.impl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.impl.config.VirtualHostConfig;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true, service = Filter.class)
@Order(-200)
@WebFilter("/*")
public final class VirtualHostFilter
    extends OncePerRequestFilter
{
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostFilter.class );

    private VirtualHostConfig config;

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( config.isEnabled() )
        {
            final VirtualHostMapping virtualHostMapping = this.config.getMappings().resolve( req );
            if ( virtualHostMapping == null )
            {
                LOG.warn( "Virtual host mapping could not be resolved for host [" + req.getServerName() + "] and path [" +  req.getRequestURI() + "]" );
                res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
            else
            {
                VirtualHostHelper.setVirtualHost( req, virtualHostMapping );
                final String targetPath = virtualHostMapping.getFullTargetPath( req );

                final RequestDispatcher dispatcher = req.getRequestDispatcher( targetPath );
                dispatcher.forward( req, res );
            }
        }
        else
        {
            final VirtualHostMapping defaultVirtualHostMapping = generateDefaultVirtualHostMapping( req );
            VirtualHostHelper.setVirtualHost( req, defaultVirtualHostMapping );
            chain.doFilter( req, res );
        }
    }

    private VirtualHostMapping generateDefaultVirtualHostMapping( final HttpServletRequest req )
    {
        final String serverName = req.getServerName();

        final VirtualHostMapping virtualHostMapping = new VirtualHostMapping( serverName );
        virtualHostMapping.setHost( serverName );
        virtualHostMapping.setSource( "/" );
        virtualHostMapping.setTarget( "/" );
        virtualHostMapping.setUserStoreKey( UserStoreKey.system() );

        return virtualHostMapping;
    }

    @Reference
    public void setConfig( final VirtualHostConfig config )
    {
        this.config = config;
    }
}
