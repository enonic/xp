package com.enonic.xp.admin.event.impl;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminEventHubImplTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ApplicationKey OWNER = ApplicationKey.from( "com.enonic.app.owner" );

    private static final ApplicationKey OTHER = ApplicationKey.from( "com.enonic.app.other" );

    private static final PrincipalKey ALLOWED_ROLE = PrincipalKey.ofRole( "allowed.role" );

    private static final String NAME = "myTopic";

    private static final String TOPIC = OWNER + ":" + NAME;

    private static final String GROUP = "admin.event.topic/" + TOPIC;

    private WebSocketManager webSocketManager;

    private EventPublisher eventPublisher;

    private AdminEventHubImpl hub;

    @BeforeEach
    void setUp()
    {
        webSocketManager = mock( WebSocketManager.class );
        eventPublisher = mock( EventPublisher.class );
        hub = new AdminEventHubImpl( webSocketManager, eventPublisher );
    }

    @Test
    void subscribeToUnknownTopicIsDeniedRetryable()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "{\"type\":\"subscribe\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        final JsonNode deny = lastSentTo( "s1" );
        assertEquals( "deny", deny.path( "type" ).asText() );
        assertEquals( "unknown", deny.path( "reason" ).asText() );
        verify( webSocketManager, never() ).addToGroup( anyString(), anyString() );
    }

    @Test
    void registerTopicReturnsCanonicalName()
    {
        assertEquals( TOPIC, hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER ) );
    }

    @Test
    void subscribeChecksAllow()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        final Session forbidden = open( "s1", PrincipalKey.ofRole( "some.other.role" ) );
        message( forbidden, subscribeFrame(), PrincipalKey.ofRole( "some.other.role" ) );
        assertEquals( "forbidden", lastSentTo( "s1" ).path( "reason" ).asText() );

        final Session allowed = open( "s2", ALLOWED_ROLE );
        message( allowed, subscribeFrame(), ALLOWED_ROLE );
        final JsonNode ack = lastSentTo( "s2" );
        assertEquals( "ack", ack.path( "type" ).asText() );
        assertEquals( 0, ack.path( "seq" ).asLong() );
        assertFalse( ack.path( "epoch" ).asText().isEmpty() );
        verify( webSocketManager ).addToGroup( GROUP, "s2" );
    }

    @Test
    void systemAdminBypassesAllow()
    {
        hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER );

        final Session admin = open( "s1", RoleKeys.ADMIN );
        message( admin, subscribeFrame(), RoleKeys.ADMIN );

        assertEquals( "ack", lastSentTo( "s1" ).path( "type" ).asText() );
    }

    @Test
    void fanOutStampsIncreasingSequence()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        hub.onEvent( topicEvent( Map.of( "n", 1 ) ) );
        hub.onEvent( topicEvent( Map.of( "n", 2 ) ) );

        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, times( 2 ) ).sendToGroup( eq( GROUP ), frames.capture() );

        final JsonNode first = parse( frames.getAllValues().get( 0 ) );
        final JsonNode second = parse( frames.getAllValues().get( 1 ) );
        assertEquals( "event", first.path( "type" ).asText() );
        assertEquals( 1, first.path( "seq" ).asLong() );
        assertEquals( 2, second.path( "seq" ).asLong() );
        assertEquals( 2, second.path( "data" ).path( "n" ).asInt() );

        // a subscriber acked after two publishes is told seq=2, so its first event is 3
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        assertEquals( 2, lastSentTo( "s1" ).path( "seq" ).asLong() );
    }

    @Test
    void eventsForUnregisteredTopicsAreNotStamped()
    {
        hub.onEvent( topicEvent( Map.of() ) );
        verify( webSocketManager, never() ).sendToGroup( anyString(), anyString() );
    }

    @Test
    void publishResolvesUnderTheCallersKey()
    {
        hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER );

        // OTHER has no topic of this name - the canonical name embeds the caller, so publishing
        // into someone else's topic is not forbidden, it is unaddressable
        assertThrows( IllegalArgumentException.class, () -> hub.publish( OTHER, NAME, Map.of() ) );
        assertThrows( IllegalArgumentException.class, () -> hub.publish( OWNER, "unregistered", Map.of() ) );

        hub.publish( OWNER, NAME, Map.of( "k", "v" ) );

        final ArgumentCaptor<Event> events = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( events.capture() );
        assertEquals( "admin.topic", events.getValue().getType() );
        assertTrue( events.getValue().isDistributed() );
        assertEquals( TOPIC, events.getValue().getData().get( "name" ) );
    }

    @Test
    void equalLocalNamesOfDifferentApplicationsAreDifferentTopics()
    {
        assertEquals( OWNER + ":" + NAME, hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER ) );
        assertEquals( OTHER + ":" + NAME, hub.registerTopic( NAME, PrincipalKeys.empty(), OTHER ) );

        hub.onEvent( Event.create( "admin.topic" ).value( "name", OTHER + ":" + NAME ).value( "data", Map.of() ).build() );

        verify( webSocketManager ).sendToGroup( eq( "admin.event.topic/" + OTHER + ":" + NAME ), anyString() );
        verify( webSocketManager, never() ).sendToGroup( eq( GROUP ), anyString() );
    }

    @Test
    void reRegistrationRevokesSubscribersFailingNewAllow()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        assertEquals( "ack", lastSentTo( "s1" ).path( "type" ).asText() );

        hub.registerTopic( NAME, PrincipalKeys.from( PrincipalKey.ofRole( "another.role" ) ), OWNER );

        final JsonNode deny = lastSentTo( "s1" );
        assertEquals( "deny", deny.path( "type" ).asText() );
        assertEquals( "forbidden", deny.path( "reason" ).asText() );
        verify( webSocketManager ).removeFromGroup( GROUP, "s1" );
    }

    @Test
    void applicationStopClearsOwnershipButKeepsSequence()
    {
        hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER );
        hub.onEvent( topicEvent( Map.of() ) );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( OWNER );
        hub.removeApplication( application );

        assertThrows( IllegalArgumentException.class, () -> hub.publish( OWNER, NAME, Map.of() ) );

        // re-registration continues the numbering: gap counting survives the redeploy
        hub.registerTopic( NAME, PrincipalKeys.empty(), OWNER );
        hub.onEvent( topicEvent( Map.of() ) );

        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, times( 2 ) ).sendToGroup( eq( GROUP ), frames.capture() );
        assertEquals( 2, parse( frames.getAllValues().get( 1 ) ).path( "seq" ).asLong() );
    }

    @Test
    void inboundRequiresAckedSubscription()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\",\"data\":{\"a\":1}}", ALLOWED_ROLE );
        assertEquals( "notSubscribed", lastSentTo( "s1" ).path( "code" ).asText() );
        verify( eventPublisher, never() ).publish( any() );

        message( session, subscribeFrame(), ALLOWED_ROLE );
        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\",\"data\":{\"a\":1},\"user\":\"user:spoofed:evil\"}",
                 ALLOWED_ROLE );

        final ArgumentCaptor<Event> events = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( events.capture() );
        final Event inbound = events.getValue();
        assertEquals( "admin.topic.in." + TOPIC, inbound.getType() );
        assertFalse( inbound.isDistributed() );
        assertEquals( "s1", inbound.getData().get( "socketId" ) );
        // identity is hub-set from the socket's auth context, never taken from the frame
        assertEquals( User.anonymous().getKey().toString(), inbound.getData().get( "user" ) );
    }

    @Test
    void duplicateSubscribeReAcks()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, times( 2 ) ).send( eq( "s1" ), frames.capture() );
        assertEquals( "ack", parse( frames.getAllValues().get( 1 ) ).path( "type" ).asText() );
        verify( webSocketManager, times( 1 ) ).addToGroup( GROUP, "s1" );
    }

    @Test
    void unsubscribeLeavesGroup()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        message( session, "{\"type\":\"unsubscribe\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        verify( webSocketManager ).removeFromGroup( GROUP, "s1" );
    }

    @Test
    void pingIsAnsweredWithPong()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "{\"type\":\"ping\"}", ALLOWED_ROLE );

        assertEquals( "pong", lastSentTo( "s1" ).path( "type" ).asText() );
    }

    @Test
    void malformedFramesAreReported()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "this is not json", ALLOWED_ROLE );
        assertEquals( "badFrame", lastSentTo( "s1" ).path( "code" ).asText() );

        message( session, "{\"type\":\"mystery\"}", ALLOWED_ROLE );
        assertEquals( "badFrame", lastSentTo( "s1" ).path( "code" ).asText() );
    }

    @Test
    void inboundIsRateLimited()
    {
        hub.registerTopic( NAME, PrincipalKeys.from( ALLOWED_ROLE ), OWNER );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        for ( int i = 0; i < 31; i++ )
        {
            message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );
        }

        // 30 accepted pubs became inbound events; the 31st was rejected with an error frame
        verify( eventPublisher, times( 30 ) ).publish( any() );
        assertEquals( "rateLimit", lastSentTo( "s1" ).path( "code" ).asText() );
    }

    @Test
    void deactivateClosesEverySocket()
        throws Exception
    {
        final Session first = open( "s1", ALLOWED_ROLE );
        final Session second = open( "s2", ALLOWED_ROLE );

        hub.deactivate();

        verify( first ).close( any( CloseReason.class ) );
        verify( second ).close( any( CloseReason.class ) );
    }

    @Test
    void errorClosesTheSession()
        throws Exception
    {
        final Session session = open( "s1", ALLOWED_ROLE );

        socketEvent( WebSocketEvent.create().type( WebSocketEventType.ERROR ).session( session ), ALLOWED_ROLE );

        verify( session ).close( any( CloseReason.class ) );
    }

    @Test
    void invalidTopicNamesAreRejected()
    {
        assertThrows( IllegalArgumentException.class, () -> hub.registerTopic( null, PrincipalKeys.empty(), OWNER ) );
        assertThrows( IllegalArgumentException.class, () -> hub.registerTopic( "  ", PrincipalKeys.empty(), OWNER ) );
        assertThrows( IllegalArgumentException.class, () -> hub.registerTopic( "with space", PrincipalKeys.empty(), OWNER ) );
        assertThrows( IllegalArgumentException.class, () -> hub.registerTopic( "with:colon", PrincipalKeys.empty(), OWNER ) );
        assertThrows( IllegalArgumentException.class, () -> hub.registerTopic( "x".repeat( 256 ), PrincipalKeys.empty(), OWNER ) );
    }

    private String subscribeFrame()
    {
        return "{\"type\":\"subscribe\",\"topic\":\"" + TOPIC + "\"}";
    }

    private static Event topicEvent( final Map<String, ?> data )
    {
        return Event.create( "admin.topic" ).distributed( true ).value( "name", TOPIC ).value( "data", data ).build();
    }

    private Session open( final String id, final PrincipalKey... principals )
    {
        final Session session = mock( Session.class );
        when( session.getId() ).thenReturn( id );
        socketEvent( WebSocketEvent.create().type( WebSocketEventType.OPEN ).session( session ), principals );
        return session;
    }

    private void message( final Session session, final String message, final PrincipalKey... principals )
    {
        socketEvent( WebSocketEvent.create().type( WebSocketEventType.MESSAGE ).session( session ).message( message ), principals );
    }

    private void socketEvent( final WebSocketEvent.Builder event, final PrincipalKey... principals )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( User.anonymous() ).principals( principals ).build();
        ContextBuilder.create().authInfo( authInfo ).build().runWith( () -> hub.onSocketEvent( event.build() ) );
    }

    private JsonNode lastSentTo( final String id )
    {
        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, atLeastOnce() ).send( eq( id ), frames.capture() );
        return parse( frames.getAllValues().get( frames.getAllValues().size() - 1 ) );
    }

    private static JsonNode parse( final String json )
    {
        try
        {
            return MAPPER.readTree( json );
        }
        catch ( Exception e )
        {
            throw new AssertionError( "Not JSON: " + json, e );
        }
    }
}
