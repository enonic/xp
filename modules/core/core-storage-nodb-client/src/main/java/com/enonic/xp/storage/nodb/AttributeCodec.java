package com.enonic.xp.storage.nodb;

import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@code spi.VersionRecord#attributes} is {@code Map<String, Object>} (raw values --
 * String/Long/Integer/Double/Boolean/List/Map, see {@code GenericValue#toRawJava()});
 * {@code nodb.proto}'s {@code Version.attributes} is {@code map<string, string>} -- the
 * engine's own {@code VersionRecord} (nodb/engine/.../model/VersionRecord.java) is
 * string-only too, so this is not a Phase-1-client shortcut, it's the wire/engine shape.
 * <p>
 * This codec JSON-encodes each attribute value to a string on write and JSON-decodes it
 * back on read, preserving full fidelity for every JSON-representable value (which covers
 * everything {@code GenericValue#toRawJava()} produces). A known Phase 1 limitation, not
 * silently lossy: values that are NOT JSON-representable would round-trip incorrectly, but
 * no such values exist in the documented attribute value set.
 */
final class AttributeCodec
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AttributeCodec()
    {
    }

    static Map<String, String> encode( final @Nullable Map<String, Object> attributes )
    {
        if ( attributes == null )
        {
            return Map.of();
        }
        final Map<String, String> result = new LinkedHashMap<>();
        attributes.forEach( ( key, value ) -> result.put( key, writeValueAsString( value ) ) );
        return result;
    }

    @Nullable
    static Map<String, Object> decode( final Map<String, String> attributes )
    {
        if ( attributes.isEmpty() )
        {
            // spi.VersionRecord: null attributes means "never stored", distinct from an
            // empty map. The wire has no such distinction (proto3 maps have no presence
            // bit), so an empty wire map is treated as "none stored" -- the common case.
            return null;
        }
        final Map<String, Object> result = new LinkedHashMap<>();
        attributes.forEach( ( key, value ) -> result.put( key, readValue( value ) ) );
        return result;
    }

    private static String writeValueAsString( final Object value )
    {
        try
        {
            return MAPPER.writeValueAsString( value );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( "Failed to encode node version attribute value: " + value, e );
        }
    }

    private static Object readValue( final String json )
    {
        try
        {
            return MAPPER.readValue( json, Object.class );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( "Failed to decode node version attribute value: " + json, e );
        }
    }
}
