package com.enonic.nodb.engine.store;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal hand-rolled JSON codec for {@code node_version.attributes} (a flat
 * string-&gt;string map). No Jackson dependency (DESIGN.md §8: closed, explicit
 * dependency graph) — the attribute list has never been more than a flat object, so a
 * tiny codec is enough for this slice.
 */
final class JsonAttributes
{
    private JsonAttributes()
    {
    }

    static String toJson( Map<String, String> attributes )
    {
        if ( attributes == null )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder( "{" );
        boolean first = true;
        for ( Map.Entry<String, String> entry : attributes.entrySet() )
        {
            if ( !first )
            {
                sb.append( ',' );
            }
            first = false;
            quote( entry.getKey(), sb );
            sb.append( ':' );
            quote( entry.getValue(), sb );
        }
        sb.append( '}' );
        return sb.toString();
    }

    static Map<String, String> fromJson( String json )
    {
        if ( json == null )
        {
            return null;
        }
        Map<String, String> result = new LinkedHashMap<>();
        int i = skipWhitespace( json, 0 );
        if ( i >= json.length() || json.charAt( i ) != '{' )
        {
            throw new IllegalArgumentException( "Not a JSON object: " + json );
        }
        i++;
        i = skipWhitespace( json, i );
        if ( i < json.length() && json.charAt( i ) == '}' )
        {
            return result;
        }
        while ( true )
        {
            i = skipWhitespace( json, i );
            int[] keyEnd = new int[1];
            String key = parseString( json, i, keyEnd );
            i = skipWhitespace( json, keyEnd[0] );
            if ( json.charAt( i ) != ':' )
            {
                throw new IllegalArgumentException( "Expected ':' at " + i + " in " + json );
            }
            i = skipWhitespace( json, i + 1 );
            int[] valueEnd = new int[1];
            String value = parseString( json, i, valueEnd );
            result.put( key, value );
            i = skipWhitespace( json, valueEnd[0] );
            if ( i < json.length() && json.charAt( i ) == ',' )
            {
                i++;
                continue;
            }
            if ( i < json.length() && json.charAt( i ) == '}' )
            {
                break;
            }
            throw new IllegalArgumentException( "Malformed JSON object: " + json );
        }
        return result;
    }

    private static int skipWhitespace( String s, int i )
    {
        while ( i < s.length() && Character.isWhitespace( s.charAt( i ) ) )
        {
            i++;
        }
        return i;
    }

    private static String parseString( String s, int i, int[] endOut )
    {
        if ( s.charAt( i ) != '"' )
        {
            throw new IllegalArgumentException( "Expected string at " + i + " in " + s );
        }
        StringBuilder sb = new StringBuilder();
        i++;
        while ( s.charAt( i ) != '"' )
        {
            char c = s.charAt( i );
            if ( c == '\\' )
            {
                i++;
                char escaped = s.charAt( i );
                switch ( escaped )
                {
                    case '"' -> sb.append( '"' );
                    case '\\' -> sb.append( '\\' );
                    case '/' -> sb.append( '/' );
                    case 'n' -> sb.append( '\n' );
                    case 'r' -> sb.append( '\r' );
                    case 't' -> sb.append( '\t' );
                    case 'u' -> {
                        String hex = s.substring( i + 1, i + 5 );
                        sb.append( (char) Integer.parseInt( hex, 16 ) );
                        i += 4;
                    }
                    default -> throw new IllegalArgumentException( "Bad escape at " + i + " in " + s );
                }
            }
            else
            {
                sb.append( c );
            }
            i++;
        }
        endOut[0] = i + 1;
        return sb.toString();
    }

    private static void quote( String s, StringBuilder sb )
    {
        sb.append( '"' );
        for ( int i = 0; i < s.length(); i++ )
        {
            char c = s.charAt( i );
            switch ( c )
            {
                case '"' -> sb.append( "\\\"" );
                case '\\' -> sb.append( "\\\\" );
                case '\n' -> sb.append( "\\n" );
                case '\r' -> sb.append( "\\r" );
                case '\t' -> sb.append( "\\t" );
                default -> {
                    if ( c < 0x20 )
                    {
                        sb.append( String.format( "\\u%04x", (int) c ) );
                    }
                    else
                    {
                        sb.append( c );
                    }
                }
            }
        }
        sb.append( '"' );
    }
}
