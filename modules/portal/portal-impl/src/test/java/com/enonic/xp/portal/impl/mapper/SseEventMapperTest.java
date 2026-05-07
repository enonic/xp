package com.enonic.xp.portal.impl.mapper;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.util.GenericValue;

class SseEventMapperTest
{
    private static final UUID CLIENT_ID = UUID.fromString( "00000000-0000-0000-0000-0000000abc12" );

    private final MapSerializableAssert assertHelper = new MapSerializableAssert( SseEventMapperTest.class );

    @Test
    void openEvent()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( CLIENT_ID )
            .lastEventId( "last-42" )
            .attributes( GenericValue.newObject().put( "userId", "user1" ).build() )
            .build();
        assertHelper.assertJson( "sseevent-open.json", new SseEventMapper( event ) );
    }

    @Test
    void errorEvent()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.ERROR )
            .clientId( CLIENT_ID )
            .attributes( GenericValue.newObject().build() )
            .error( new RuntimeException( "connection lost" ) )
            .build();
        assertHelper.assertJson( "sseevent-error.json", new SseEventMapper( event ) );
    }

    @Test
    void closeEvent()
    {
        final SseEvent event =
            SseEvent.create().type( SseEventType.CLOSE ).clientId( CLIENT_ID ).attributes( GenericValue.newObject().build() ).build();
        assertHelper.assertJson( "sseevent-close.json", new SseEventMapper( event ) );
    }
}
