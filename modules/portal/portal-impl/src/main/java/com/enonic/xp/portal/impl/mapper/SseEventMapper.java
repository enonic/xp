package com.enonic.xp.portal.impl.mapper;

import java.util.Map;

import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class SseEventMapper
    implements MapSerializable
{
    private final SseEvent event;

    public SseEventMapper( final SseEvent event )
    {
        this.event = event;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "type", this.event.getType().toString().toLowerCase() );
        serializeSession( gen );
        serializeData( gen );
        serializeError( gen );
    }

    private void serializeSession( final MapGenerator gen )
    {
        gen.map( "session" );
        gen.value( "id", this.event.getId() );
        gen.end();
    }

    private void serializeData( final MapGenerator gen )
    {
        final Map<String, String> data = this.event.getData();
        if ( data == null || data.isEmpty() )
        {
            return;
        }

        gen.map( "data" );
        for ( final Map.Entry<String, String> entry : data.entrySet() )
        {
            gen.value( entry.getKey(), entry.getValue() );
        }
        gen.end();
    }

    private void serializeError( final MapGenerator gen )
    {
        final Throwable error = this.event.getError();
        if ( error == null )
        {
            return;
        }

        gen.value( "error", error.getMessage() );
    }
}
