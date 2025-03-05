package com.enonic.xp.lib.portal.url;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.enonic.xp.script.ScriptValue;

final class UrlHandlerHelper
{
    private UrlHandlerHelper()
    {
    }

    public static Map<String, Collection<String>> resolveQueryParams( final ScriptValue params )
    {
        if ( params == null )
        {
            return null;
        }

        final Map<String, Collection<String>> result = new LinkedHashMap<>();

        for ( final Map.Entry<String, Object> param : params.getMap().entrySet() )
        {
            final Object value = param.getValue();
            if ( value instanceof Iterable<?> values )
            {
                for ( final Object v : values )
                {
                    result.computeIfAbsent( param.getKey(), k -> new ArrayList<>() ).add( v.toString() );
                }
            }
            else
            {
                result.computeIfAbsent( param.getKey(), k -> new ArrayList<>() ).add( value.toString() );
            }
        }

        return result;
    }
}
