package com.enonic.xp.portal.impl.controller;

import java.util.Locale;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Helps to convert XP Portal sameSite cookie value into Jetty comment value.
 * Servlet 3.1 does not support SameSite cookie flag. This is Jetty's workaround.
 */
public final class SameSiteCommentHelper
{
    private SameSiteCommentHelper()
    {
    }

    public static String convert( final String sameSiteValue )
    {
        if ( !nullToEmpty( sameSiteValue ).isEmpty() )
        {
            switch ( sameSiteValue.toLowerCase( Locale.ROOT ) )
            {
                case "none":
                    return "__SAME_SITE_NONE__";
                case "lax":
                    return "__SAME_SITE_LAX__";
                case "strict":
                    return "__SAME_SITE_STRICT__";
                default:
                    throw new IllegalArgumentException( "Unsupported sameSite " + sameSiteValue );
            }
        }
        else
        {
            return "";
        }
    }
}
