package com.enonic.xp.portal.impl.url;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Splitter;

public final class UrlHelper
{
    public static Map<String, String> extractUrlParams( String url )
    {
        String[] urlParts = url.split( "\\?" );
        if ( urlParts.length > 1 )
        {
            return Splitter.on( '&' ).trimResults().withKeyValueSeparator( "=" ).split( urlParts[1].replace( "&amp;", "&" ) );
        }
        else
        {
            return new HashMap<String, String>();
        }

    }
}
