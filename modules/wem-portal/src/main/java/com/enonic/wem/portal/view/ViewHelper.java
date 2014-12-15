package com.enonic.wem.portal.view;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public final class ViewHelper
{
    public static Multimap<String, String> toParamMap( final String... params )
    {
        return toParamMap( Lists.newArrayList( params ) );
    }

    public static Multimap<String, String> toParamMap( final List<String> params )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final String param : params )
        {
            addParam( map, param );
        }

        return map;
    }

    private static void addParam( final Multimap<String, String> map, final String param )
    {
        final int pos = param.indexOf( '=' );
        if ( ( pos <= 0 ) || ( pos >= param.length() ) )
        {
            return;
        }

        final String key = param.substring( 0, pos ).trim();
        final String value = param.substring( pos + 1 ).trim();
        map.put( key, value );
    }
}
