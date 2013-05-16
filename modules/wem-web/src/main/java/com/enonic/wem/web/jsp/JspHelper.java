package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;

import com.enonic.wem.api.Version;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class JspHelper
{
    public static String getProductVersion()
    {
        return Version.get().getNameAndVersion();
    }

    public static String getBaseUrl( final HttpServletRequest req )
    {
        return createUrl( req, null );
    }

    public static String createUrl( final HttpServletRequest req, final String path )
    {
        return ServletRequestUrlHelper.createUrl( req, path );
    }

    public static String ellipsis( final String text, final int length )
    {
        if ( text.length() <= length )
        {
            return text;
        }
        else
        {
            final String outStr = Splitter.fixedLength( length ).split( text ).iterator().next();
            return outStr + "...";
        }
    }
}
