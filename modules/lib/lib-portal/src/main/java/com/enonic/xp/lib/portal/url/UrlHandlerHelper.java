package com.enonic.xp.lib.portal.url;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.script.ScriptValue;

final class UrlHandlerHelper
{
    private UrlHandlerHelper()
    {
    }

    public static Multimap<String, String> resolveQueryParams( final ScriptValue params )
    {
        if ( params == null )
        {
            return null;
        }

        final Multimap<String, String> result = HashMultimap.create();

        for ( final Map.Entry<String, Object> param : params.getMap().entrySet() )
        {
            final Object value = param.getValue();
            if ( value instanceof Iterable<?> values )
            {
                for ( final Object v : values )
                {
                    result.put( param.getKey(), v.toString() );
                }
            }
            else
            {
                result.put( param.getKey(), value.toString() );
            }
        }

        return result;
    }
}
