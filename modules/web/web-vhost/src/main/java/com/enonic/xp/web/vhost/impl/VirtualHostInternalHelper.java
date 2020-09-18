package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;

import com.enonic.xp.web.vhost.VirtualHost;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class VirtualHostInternalHelper
{

    public static String normalizePath( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return "/";
        }

        final Iterable<String> parts = Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value );
        return "/" + String.join( "/", parts );
    }

    public static String getFullTargetPath( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        String path = req.getRequestURI();
        if ( !"/".equals( virtualHost.getSource() ) && path.startsWith( virtualHost.getSource() ) )
        {
            path = path.substring( virtualHost.getSource().length() );
        }

        return normalizePath( virtualHost.getTarget() + path );
    }

}
