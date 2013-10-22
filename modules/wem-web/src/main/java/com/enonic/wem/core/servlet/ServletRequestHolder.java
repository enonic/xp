package com.enonic.wem.core.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;

public final class ServletRequestHolder
{
    private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<>();

    public static HttpServletRequest getRequest()
    {
        final HttpServletRequest req = CURRENT_REQUEST.get();
        Preconditions.checkState( req != null, "No request bound to thread" );
        return req;
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
