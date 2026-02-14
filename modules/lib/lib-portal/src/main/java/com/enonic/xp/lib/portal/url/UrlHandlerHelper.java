package com.enonic.xp.lib.portal.url;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
                    result.computeIfAbsent( param.getKey(), k -> new ArrayList<>() ).add( serializeValue( v ) );
                }
            }
            else
            {
                result.computeIfAbsent( param.getKey(), k -> new ArrayList<>() ).add( serializeValue( value ) );
            }
        }

        return result;
    }

    private static String serializeValue( final Object value )
    {
        if ( value == null )
        {
            return "null";
        }

        if ( value instanceof String || value instanceof Number || value instanceof Boolean )
        {
            return value.toString();
        }

        if ( value instanceof Map )
        {
            return serializeMap( (Map<?, ?>) value );
        }

        if ( value instanceof List )
        {
            return serializeList( (List<?>) value );
        }

        return value.toString();
    }

    private static String serializeMap( final Map<?, ?> map )
    {
        final StringBuilder sb = new StringBuilder( "{" );
        boolean first = true;

        for ( final Map.Entry<?, ?> entry : map.entrySet() )
        {
            if ( !first )
            {
                sb.append( "," );
            }
            first = false;

            sb.append( "\"" ).append( escapeJson( String.valueOf( entry.getKey() ) ) ).append( "\":" );
            sb.append( serializeValueAsJson( entry.getValue() ) );
        }

        sb.append( "}" );
        return sb.toString();
    }

    private static String serializeList( final List<?> list )
    {
        final StringBuilder sb = new StringBuilder( "[" );
        boolean first = true;

        for ( final Object item : list )
        {
            if ( !first )
            {
                sb.append( "," );
            }
            first = false;

            sb.append( serializeValueAsJson( item ) );
        }

        sb.append( "]" );
        return sb.toString();
    }

    private static String serializeValueAsJson( final Object value )
    {
        if ( value == null )
        {
            return "null";
        }

        if ( value instanceof String )
        {
            return "\"" + escapeJson( (String) value ) + "\"";
        }

        if ( value instanceof Number || value instanceof Boolean )
        {
            return value.toString();
        }

        if ( value instanceof Map )
        {
            return serializeMap( (Map<?, ?>) value );
        }

        if ( value instanceof List )
        {
            return serializeList( (List<?>) value );
        }

        return "\"" + escapeJson( value.toString() ) + "\"";
    }

    private static String escapeJson( final String value )
    {
        return value.replace( "\\", "\\\\" )
            .replace( "\"", "\\\"" )
            .replace( "\b", "\\b" )
            .replace( "\f", "\\f" )
            .replace( "\n", "\\n" )
            .replace( "\r", "\\r" )
            .replace( "\t", "\\t" );
    }
}
