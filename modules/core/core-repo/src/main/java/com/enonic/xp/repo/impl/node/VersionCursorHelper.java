package com.enonic.xp.repo.impl.node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.node.NodeVersionId;

public final class VersionCursorHelper
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private VersionCursorHelper()
    {
    }

    public record CursorData(Instant ts, NodeVersionId id)
    {
    }

    public static CursorData decodeCursor( final String cursor )
    {
        try
        {
            final byte[] decoded = Base64.getDecoder().decode( cursor );
            final JsonNode json = MAPPER.readTree( decoded );
            final Instant timestamp = Instant.parse( json.get( "ts" ).asText() );
            final NodeVersionId id = NodeVersionId.from( json.get( "id" ).asText() );
            return new CursorData( timestamp, id );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Failed to decode cursor", e );
        }
    }

    public static String encodeCursor( final CursorData cursorData )
    {
        try
        {
            final ObjectNode json = MAPPER.createObjectNode();
            json.put( "ts", cursorData.ts.toString() );
            json.put( "id", cursorData.id.toString() );
            return Base64.getEncoder().encodeToString( MAPPER.writeValueAsBytes( json ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Failed to encode cursor", e );
        }
    }
}
