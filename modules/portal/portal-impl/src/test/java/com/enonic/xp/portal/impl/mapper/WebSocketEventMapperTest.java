package com.enonic.xp.portal.impl.mapper;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import jakarta.websocket.Session;

import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.security.User;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSocketEventMapperTest
{
    private final MapSerializableAssert assertHelper = new MapSerializableAssert( WebSocketEventMapperTest.class );

    @Test
    void user()
        throws Exception
    {
        final Session session = mock( Session.class );
        when( session.getRequestURI() ).thenReturn( URI.create( "http://localhost/ws" ) );
        when( session.getId() ).thenReturn( "1" );
        when( session.getUserPrincipal() ).thenReturn( User.ANONYMOUS );
        final WebSocketEvent webSocketEvent = WebSocketEvent.create().
            type( WebSocketEventType.MESSAGE ).
            session( session ).
            data( new TreeMap<>( Map.of( "a", "b", "c", "d") ) ).
            build();
        assertHelper.assertJson( "websocketevent-user.json", new WebSocketEventMapper( webSocketEvent ) );
    }
}
