package com.enonic.xp.lib.portal.url;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.enonic.xp.script.ScriptValue;

final class UrlHandlerHelper
{
    private UrlHandlerHelper()
    {
    }

    public static Map<String, List<String>> resolveQueryParams( final ScriptValue params )
    {
        if ( params == null )
        {
            return null;
        }

        final Map<String, List<String>> result = new LinkedHashMap<>();

        for ( final Map.Entry<String, Object> param : params.getMap().entrySet() )
        {
            final Object value = param.getValue();
            if ( value instanceof Iterable<?> values )
            {
                result.put( param.getKey(), StreamSupport.stream( values.spliterator(), false )
                    .map( Object::toString )
                    .collect( Collectors.toUnmodifiableList() ) );
            }
            else
            {
                result.put( param.getKey(), List.of( value.toString() ) );
            }
        }

        return result;
    }
}
