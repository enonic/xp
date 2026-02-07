package com.enonic.xp.web.vhost.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.vhost.VirtualHost;

final class VirtualHostRequestWrapper
    extends HttpServletRequestWrapper
{
    private final String requestUri;

    private final String pathInfo;

    VirtualHostRequestWrapper( final HttpServletRequest request, final VirtualHost virtualHost )
    {
        super( request );
        this.requestUri = applyVhostMapping( virtualHost, request.getRequestURI() );
        this.pathInfo = applyVhostMapping( virtualHost, request.getPathInfo() );
    }

    @Override
    public String getRequestURI()
    {
        return requestUri;
    }

    @Override
    public StringBuffer getRequestURL()
    {
        return new StringBuffer( ServletRequestUrlHelper.getServerUrl( this ) ).append( this.requestUri );
    }

    @Override
    public String getPathInfo()
    {
        return pathInfo;
    }

    static String applyVhostMapping( final VirtualHost virtualHost, final String path )
    {
        final String source = virtualHost.getSource();
        final String target = virtualHost.getTarget();

        if ( "/".equals( source ) )
        {
            return "/".equals( target ) ? path : target + path;
        }

        if ( "/".equals( target ) )
        {
            return path.equals( source ) ? "/" : path.substring( source.length() );
        }

        return target + path.substring( source.length() );
    }
}
