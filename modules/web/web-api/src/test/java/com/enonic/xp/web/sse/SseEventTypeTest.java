package com.enonic.xp.web.sse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SseEventTypeTest
{
    @Test
    void value_returnsLowercase()
    {
        assertThat( SseEventType.OPEN.value() ).isEqualTo( "open" );
        assertThat( SseEventType.CLOSE.value() ).isEqualTo( "close" );
        assertThat( SseEventType.TIMEOUT.value() ).isEqualTo( "timeout" );
        assertThat( SseEventType.ERROR.value() ).isEqualTo( "error" );
    }

    @Test
    void toString_equalsValue()
    {
        for ( final SseEventType type : SseEventType.values() )
        {
            assertThat( type.toString() ).isEqualTo( type.value() );
        }
    }
}
