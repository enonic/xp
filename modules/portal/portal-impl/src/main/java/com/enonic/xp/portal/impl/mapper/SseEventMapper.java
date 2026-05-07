package com.enonic.xp.portal.impl.mapper;

import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.util.GenericValue;

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
        gen.value( "type", this.event.getType().value() );
        gen.value( "clientId", this.event.getClientId().toString() );
        final String lastEventId = this.event.getLastEventId();
        if ( lastEventId != null )
        {
            gen.value( "lastEventId", lastEventId );
        }
        final GenericValue attributes = this.event.getAttributes();
        if ( !attributes.properties().isEmpty() )
        {
            gen.value( "attributes", attributes.toRawJs() );
        }
        serializeError( gen );
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
