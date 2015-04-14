package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;

@Beta
public final class ServletRequestHolder
{
    private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<>();

    public static HttpServletRequest getRequest()
    {
        return CURRENT_REQUEST.get();
    }

    public static void setRequest( final HttpServletRequest req )
    {
        if ( req == null )
        {
            CURRENT_REQUEST.remove();
        }
        else
        {
            CURRENT_REQUEST.set( req );
        }
    }
}
