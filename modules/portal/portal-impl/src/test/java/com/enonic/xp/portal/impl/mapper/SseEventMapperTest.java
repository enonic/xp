package com.enonic.xp.portal.impl.mapper;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.portal.sse.SseEventType;

class SseEventMapperTest
{
    private final MapSerializableAssert assertHelper = new MapSerializableAssert( SseEventMapperTest.class );

    @Test
    void connectEvent()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.CONNECT )
            .id( "abc123" )
            .data( new TreeMap<>( Map.of( "userId", "user1" ) ) )
            .build();
        assertHelper.assertJson( "sseevent-connect.json", new SseEventMapper( event ) );
    }

    @Test
    void errorEvent()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.ERROR )
            .id( "abc123" )
            .error( new RuntimeException( "connection lost" ) )
            .build();
        assertHelper.assertJson( "sseevent-error.json", new SseEventMapper( event ) );
    }

    @Test
    void closeEvent()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.CLOSE )
            .id( "abc123" )
            .build();
        assertHelper.assertJson( "sseevent-close.json", new SseEventMapper( event ) );
    }
}
