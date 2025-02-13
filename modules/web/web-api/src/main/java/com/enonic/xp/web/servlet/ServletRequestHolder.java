package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ServletRequestHolder
{
    private static final ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<>();

    @Deprecated
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
