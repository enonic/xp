package com.enonic.xp.web.sse;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SseEventTest
{
    private static final UUID CLIENT_ID = UUID.randomUUID();

    private static final GenericValue ATTRIBUTES = GenericValue.newObject().build();

    @Test
    void builder_minimal()
    {
        final SseEvent event = SseEvent.create().clientId( CLIENT_ID ).attributes( ATTRIBUTES ).build();
        assertThat( event.getType() ).isEqualTo( SseEventType.OPEN );
        assertThat( event.getClientId() ).isSameAs( CLIENT_ID );
        assertThat( event.getAttributes() ).isSameAs( ATTRIBUTES );
        assertThat( event.getLastEventId() ).isNull();
        assertThat( event.getError() ).isNull();
    }

    @Test
    void builder_fullyPopulated()
    {
        final RuntimeException err = new RuntimeException( "boom" );
        final SseEvent event = SseEvent.create()
            .type( SseEventType.ERROR )
            .clientId( CLIENT_ID )
            .lastEventId( "42" )
            .attributes( ATTRIBUTES )
            .error( err )
            .build();
        assertThat( event.getType() ).isEqualTo( SseEventType.ERROR );
        assertThat( event.getLastEventId() ).isEqualTo( "42" );
        assertThat( event.getError() ).isSameAs( err );
    }

    @Test
    void clientId_required()
    {
        assertThatThrownBy( () -> SseEvent.create().attributes( ATTRIBUTES ).build() ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void attributes_required()
    {
        assertThatThrownBy( () -> SseEvent.create().clientId( CLIENT_ID ).build() ).isInstanceOf( NullPointerException.class );
    }
}
