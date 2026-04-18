package com.enonic.xp.portal.impl.controller;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalResponseSerializerSseTest
{
    @Test
    void populateSse_allFields()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue sse = mock( ScriptValue.class );
        when( root.getMember( "sse" ) ).thenReturn( sse );

        final ScriptValue attributes = mock( ScriptValue.class );
        when( sse.getMember( "attributes" ) ).thenReturn( attributes );
        when( attributes.getMap() ).thenReturn( Map.of( "user", "alice", "count", 42 ) );

        final ScriptValue retry = mock( ScriptValue.class );
        when( sse.getMember( "retry" ) ).thenReturn( retry );
        when( retry.getValue( Double.class ) ).thenReturn( 5000.0 );

        final ScriptValue timeout = mock( ScriptValue.class );
        when( sse.getMember( "timeout" ) ).thenReturn( timeout );
        when( timeout.getValue( Double.class ) ).thenReturn( 60000.0 );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getSse() ).isNotNull();
        assertThat( response.getSse().retry() ).isEqualTo( 5000L );
        assertThat( response.getSse().timeout() ).isEqualTo( 60000L );
        assertThat( response.getSse().attributes().getType() ).isEqualTo( GenericValue.Type.OBJECT );
        assertThat( response.getSse().attributes().property( "user" ).asString() ).isEqualTo( "alice" );
    }

    @Test
    void populateSse_defaults()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue sse = mock( ScriptValue.class );
        when( root.getMember( "sse" ) ).thenReturn( sse );
        // no attributes / retry / timeout members

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getSse() ).isNotNull();
        assertThat( response.getSse().retry() ).isEqualTo( -1L );
        assertThat( response.getSse().timeout() ).isEqualTo( 0L );
        assertThat( response.getSse().attributes().properties() ).isEmpty();
    }

    @Test
    void populateSse_absent()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getSse() ).isNull();
    }
}
