package com.enonic.xp.mcp.impl;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

@NullMarked
record McpArguments(Map<String, @Nullable Object> map)
    implements MapSerializable
{
    private static final long JS_MAX_SAFE_INTEGER = ( 1L << 53 ) - 1;

    @Override
    public void serialize( final MapGenerator gen )
    {
        serializeEntries( gen, map );
    }

    private static void serializeEntries( final MapGenerator gen, final Map<?, ?> entries )
    {
        entries.forEach( ( key, value ) -> {
            final var name = String.valueOf( key );
            switch ( value )
            {
                case Map<?, ?> nested ->
                {
                    gen.map( name );
                    serializeEntries( gen, nested );
                    gen.end();
                }
                case List<?> items ->
                {
                    gen.array( name );
                    serializeItems( gen, items );
                    gen.end();
                }
                case null, default -> gen.rawValue( name, scalar( value ) );
            }
        } );
    }

    private static void serializeItems( final MapGenerator gen, final List<?> items )
    {
        for ( final var item : items )
        {
            switch ( item )
            {
                case Map<?, ?> nested ->
                {
                    gen.map();
                    serializeEntries( gen, nested );
                    gen.end();
                }
                case List<?> inner ->
                {
                    gen.array();
                    serializeItems( gen, inner );
                    gen.end();
                }
                case null, default -> gen.rawValue( scalar( item ) );
            }
        }
    }

    private static @Nullable Object scalar( final @Nullable Object value )
    {
        return switch ( value )
        {
            case Integer _, Double _ -> value;
            case Long number when number >= -JS_MAX_SAFE_INTEGER && number <= JS_MAX_SAFE_INTEGER -> value;
            case Number number -> number.doubleValue();
            case null, default -> value;
        };
    }
}
