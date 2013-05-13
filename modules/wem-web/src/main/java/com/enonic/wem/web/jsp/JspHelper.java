package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.enonic.wem.api.Version;

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
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromContextPath( req );
        if ( !Strings.isNullOrEmpty( path ) )
        {
            if ( '/' == path.charAt( 0 ) )
            {
                builder.pathSegment( path.substring( 1 ) );
            }
            else
            {
                builder.pathSegment( path );
            }
        }
        return builder.build().toString();
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
