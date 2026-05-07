package com.enonic.xp.web.sse;

import org.junit.jupiter.api.Test;

import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SseConfigTest
{
    @Test
    void empty_defaults()
    {
        final SseConfig config = SseConfig.empty();
        assertThat( config.attributes().getType() ).isEqualTo( GenericValue.Type.OBJECT );
        assertThat( config.attributes().properties() ).isEmpty();
        assertThat( config.retry() ).isEqualTo( -1 );
        assertThat( config.timeout() ).isEqualTo( 0 );
    }

    @Test
    void attributes_mustBeObject()
    {
        assertThatThrownBy( () -> new SseConfig( GenericValue.stringValue( "x" ), -1, 0 ) ).isInstanceOf(
            IllegalArgumentException.class ).hasMessageContaining( "OBJECT" );
    }

    @Test
    void attributes_null_throws()
    {
        assertThatThrownBy( () -> new SseConfig( null, -1, 0 ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void acceptsNonEmptyObject()
    {
        final GenericValue attrs = GenericValue.newObject().put( "k", "v" ).build();
        final SseConfig config = new SseConfig( attrs, 5000, 60000 );
        assertThat( config.attributes() ).isSameAs( attrs );
        assertThat( config.retry() ).isEqualTo( 5000 );
        assertThat( config.timeout() ).isEqualTo( 60000 );
    }
}
