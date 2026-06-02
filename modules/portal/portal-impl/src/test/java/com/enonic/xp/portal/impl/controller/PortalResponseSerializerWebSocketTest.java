package com.enonic.xp.portal.impl.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.script.ScriptValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PortalResponseSerializerWebSocketTest
{
    @Test
    void absent_webSocket_leaves_config_null()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getWebSocket() ).isNull();
    }

    @Test
    void populate_webSocket_subProtocols_and_data()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue webSocket = mock( ScriptValue.class );
        when( root.getMember( "webSocket" ) ).thenReturn( webSocket );

        final ScriptValue subProtocols = mock( ScriptValue.class );
        when( webSocket.getMember( "subProtocols" ) ).thenReturn( subProtocols );
        when( subProtocols.isArray() ).thenReturn( true );
        when( subProtocols.getArray( String.class ) ).thenReturn( List.of( "text" ) );

        final ScriptValue data = mock( ScriptValue.class );
        when( webSocket.getMember( "data" ) ).thenReturn( data );
        when( data.getMap() ).thenReturn( Map.of( "k", "v" ) );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getWebSocket() ).isNotNull();
        assertThat( response.getWebSocket().getSubProtocols() ).containsExactly( "text" );
        assertThat( response.getWebSocket().getData() ).containsEntry( "k", "v" );
        assertThat( response.getWebSocket().getOriginValidator() ).isNull();
    }

    @Test
    void populate_webSocket_checkOrigin_function_wraps_into_predicate()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue webSocket = mock( ScriptValue.class );
        when( root.getMember( "webSocket" ) ).thenReturn( webSocket );

        final ScriptValue checkOrigin = mock( ScriptValue.class );
        when( webSocket.getMember( "checkOrigin" ) ).thenReturn( checkOrigin );
        when( checkOrigin.isFunction() ).thenReturn( true );

        final ScriptValue trueResult = mock( ScriptValue.class );
        when( trueResult.getValue( Boolean.class ) ).thenReturn( Boolean.TRUE );
        when( checkOrigin.call( "https://example.com" ) ).thenReturn( trueResult );

        final ScriptValue falseResult = mock( ScriptValue.class );
        when( falseResult.getValue( Boolean.class ) ).thenReturn( Boolean.FALSE );
        when( checkOrigin.call( "https://evil.example.org" ) ).thenReturn( falseResult );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        final Predicate<String> validator = response.getWebSocket().getOriginValidator();
        assertThat( validator ).isNotNull();
        assertThat( validator.test( "https://example.com" ) ).isTrue();
        assertThat( validator.test( "https://evil.example.org" ) ).isFalse();
        verify( checkOrigin ).call( eq( "https://example.com" ) );
        verify( checkOrigin ).call( eq( "https://evil.example.org" ) );
    }

    @Test
    void checkOrigin_non_function_value_is_ignored()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue webSocket = mock( ScriptValue.class );
        when( root.getMember( "webSocket" ) ).thenReturn( webSocket );

        final ScriptValue checkOrigin = mock( ScriptValue.class );
        when( webSocket.getMember( "checkOrigin" ) ).thenReturn( checkOrigin );
        when( checkOrigin.isFunction() ).thenReturn( false );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getWebSocket() ).isNotNull();
        assertThat( response.getWebSocket().getOriginValidator() ).isNull();
    }

    @Test
    void checkOrigin_function_returning_null_treated_as_false()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue webSocket = mock( ScriptValue.class );
        when( root.getMember( "webSocket" ) ).thenReturn( webSocket );

        final ScriptValue checkOrigin = mock( ScriptValue.class );
        when( webSocket.getMember( "checkOrigin" ) ).thenReturn( checkOrigin );
        when( checkOrigin.isFunction() ).thenReturn( true );
        when( checkOrigin.call( "https://example.com" ) ).thenReturn( null );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getWebSocket().getOriginValidator().test( "https://example.com" ) ).isFalse();
    }

    @Test
    void checkOrigin_function_that_throws_rejects_upgrade()
    {
        final ScriptValue root = mock( ScriptValue.class );
        when( root.isObject() ).thenReturn( true );

        final ScriptValue webSocket = mock( ScriptValue.class );
        when( root.getMember( "webSocket" ) ).thenReturn( webSocket );

        final ScriptValue checkOrigin = mock( ScriptValue.class );
        when( webSocket.getMember( "checkOrigin" ) ).thenReturn( checkOrigin );
        when( checkOrigin.isFunction() ).thenReturn( true );
        when( checkOrigin.call( "https://example.com" ) ).thenThrow( new RuntimeException( "boom" ) );

        final PortalResponse response = new PortalResponseSerializer( root ).serialize();

        assertThat( response.getWebSocket().getOriginValidator().test( "https://example.com" ) ).isFalse();
    }
}
