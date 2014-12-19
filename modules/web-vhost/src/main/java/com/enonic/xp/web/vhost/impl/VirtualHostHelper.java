package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

public final class VirtualHostHelper
{
    private final static String BASE_PATH = VirtualHostHelper.class.getPackage().getName() + ".basePath";

    public static boolean hasBasePath( final HttpServletRequest request )
    {
        return getBasePath( request ) != null;
    }

    public static String getBasePath( final HttpServletRequest request )
    {
        return (String) request.getAttribute( BASE_PATH );
    }

    public static void setBasePath( final HttpServletRequest request, final String basePath )
    {
        request.setAttribute( BASE_PATH, basePath );
    }
}
