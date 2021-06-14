package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;

public final class VirtualHostInternalHelper
{

    public static String getFullTargetPath( final VirtualHost virtualHost, final HttpServletRequest req )
    {
        String path = req.getRequestURI();
        if ( !"/".equals( virtualHost.getSource() ) && path.startsWith( virtualHost.getSource() ) )
        {
            path = path.substring( virtualHost.getSource().length() );
        }

        return virtualHost.getTarget() + path;
    }
}
