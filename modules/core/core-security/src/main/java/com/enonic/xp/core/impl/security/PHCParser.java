package com.enonic.xp.core.impl.security;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PHCParser
{
    private static final Pattern PHC_PATTERN = Pattern.compile(
        "^\\$" + "(?<id>[^$]+)" + "(?:\\$(?<version>v=\\d+))?" + "(?:\\$(?<params>[^$=]+=[^$]+(?:,[^$=]+=[^$]+)*))?" +
            "(?:\\$(?<salt>[^$=]+))?" + "(?:\\$(?<hash>[^$=]+))?" + "\\$?$" );

    private PHCParser()
    {
    }

    static PHCData parse( final String phcString )
    {
        final Matcher m = PHC_PATTERN.matcher( phcString );

        if ( !m.matches() )
        {
            throw new IllegalArgumentException( "Could not parse PHC string; wrong format" );
        }

        final String id = m.group( "id" );

        Integer version = null;
        final String v = m.group( "version" );
        if ( v != null )
        {
            version = Integer.parseInt( v.substring( 2 ) );
        }

        final Map<String, String> params = new HashMap<>();
        final String p = m.group( "params" );
        if ( p != null )
        {
            for ( String param : p.split( "," ) )
            {
                final int eq = param.indexOf( '=' );
                params.put( param.substring( 0, eq ), param.substring( eq + 1 ) );
            }
        }

        final Base64.Decoder decoder = Base64.getDecoder();

        byte[] salt = null;
        if ( m.group( "salt" ) != null )
        {
            salt = decoder.decode( m.group( "salt" ) );
        }

        byte[] hash = null;
        if ( m.group( "hash" ) != null )
        {
            hash = decoder.decode( m.group( "hash" ) );
        }

        return new PHCData( id, version, Map.copyOf( params ), salt, hash );
    }

    record PHCData(String id, Integer version, Map<String, String> params, byte[] salt, byte[] hash)
    {
        int paramInt( String param )
        {
            return Integer.parseInt( Objects.requireNonNull( params.get( param ), "PHC string missing required parameter: " + param ) );
        }
    }
}
