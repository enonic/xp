package com.enonic.xp.web.vhost.impl;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;

public final class VirtualHostInternalHelper
{

    private VirtualHostInternalHelper()
    {
    }

    public static String getFullTargetPath( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        final String source = virtualHost.getSource();
        final String target = virtualHost.getTarget();
        final String requestURI = req.getRequestURI();

        if ( source.equals( requestURI ) )
        {
            return target;
        }

        if ( "/".equals( source ) )
        {
            return "/".equals( target ) ? requestURI : target + requestURI;
        }

        if ( "/".equals( target ) )
        {
            return requestURI.substring( source.length() );
        }

        return target + requestURI.substring( source.length() );
    }
}
