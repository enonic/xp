package com.enonic.xp.mcp.impl;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.descriptor.DescriptorKey;

@NullMarked
public final class McpNames
{
    private McpNames()
    {
    }

    public static String qualify( final DescriptorKey key, final String localName )
    {
        final var name = encode( key.getApplicationKey().toString() ) + "__" + encode( key.getName() ) + "__" + encode( localName );
        if ( name.length() > 128 )
        {
            throw new IllegalArgumentException( "Public MCP name for " + key + " member '" + localName + "' has " + name.length() +
                                                    " characters; maximum is 128. Shorten the application, group or member name." );
        }
        return name;
    }

    private static String encode( final String component )
    {
        final var encoded = new StringBuilder();
        for ( int i = 0; i < component.length(); i++ )
        {
            final char c = component.charAt( i );
            if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '.' || c == '-' )
            {
                encoded.append( c );
            }
            else if ( c == '_' )
            {
                encoded.append( "_u" );
            }
            else
            {
                encoded.append( "_x" );
                for ( int shift = 12; shift >= 0; shift -= 4 )
                {
                    encoded.append( Character.forDigit( ( c >> shift ) & 0xf, 16 ) );
                }
            }
        }
        return encoded.toString();
    }
}
