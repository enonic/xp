package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import com.enonic.xp.admin.event.PublishMessageParams;
import com.enonic.xp.admin.event.SetTopicParams;
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
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketContext;
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
import static org.mockito.Mockito.doThrow;
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

    private WebSocketManager webSocketManager;

    private EventPublisher eventPublisher;

    private AdminEventHubImpl hub;

    private AdminEventTopics topicRegistry;

    private long clock;

    @BeforeEach
    void setUp()
    {
        webSocketManager = mock( WebSocketManager.class );
        eventPublisher = mock( EventPublisher.class );
        topicRegistry = new AdminEventTopics();
        hub = new AdminEventHubImpl( webSocketManager, eventPublisher, topicRegistry );
        AdminEventHubImpl.nanoTime = () -> clock;
    }

    @AfterEach
    void tearDown()
    {
        AdminEventHubImpl.nanoTime = System::nanoTime;
    }

    @Test
    void subscribeToUnknownTopicIsDeniedRetryable()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "{\"type\":\"subscribe\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        final JsonNode deny = lastSentTo( "s1" );
        assertEquals( "deny", deny.path( "type" ).asText() );
        assertEquals( "unknown", deny.path( "reason" ).asText() );
        assertEquals( 1, framesTo( "s1" ).size() );
    }

    @Test
    void setTopicReturnsCanonicalName()
    {
        assertEquals( TOPIC, hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
    }

    @Test
    void subscribeChecksAllow()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Session forbidden = open( "s1", PrincipalKey.ofRole( "some.other.role" ) );
        message( forbidden, subscribeFrame(), PrincipalKey.ofRole( "some.other.role" ) );
        assertEquals( "forbidden", lastSentTo( "s1" ).path( "reason" ).asText() );

        final Session allowed = open( "s2", ALLOWED_ROLE );
        message( allowed, subscribeFrame(), ALLOWED_ROLE );
        final JsonNode ack = lastSentTo( "s2" );
        assertEquals( "ack", ack.path( "type" ).asText() );
        assertEquals( 0, ack.path( "seq" ).asLong() );
        assertFalse( ack.path( "epoch" ).asText().isEmpty() );

        // the ack is what joins delivery, so the next event reaches s2 and not the denied s1
        hub.onEvent( topicEvent( Map.of() ) );
        assertEquals( "event", lastSentTo( "s2" ).path( "type" ).asText() );
        assertEquals( "deny", lastSentTo( "s1" ).path( "type" ).asText() );
    }

    @Test
    void systemAdminBypassesAllow()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Session admin = open( "s1", RoleKeys.ADMIN );
        message( admin, subscribeFrame(), RoleKeys.ADMIN );

        assertEquals( "ack", lastSentTo( "s1" ).path( "type" ).asText() );
    }

    @Test
    void fanOutStampsIncreasingSequence()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        hub.onEvent( topicEvent( Map.of( "n", 1 ) ) );
        hub.onEvent( topicEvent( Map.of( "n", 2 ) ) );

        final List<JsonNode> events = eventsTo( "s1" );
        assertEquals( 2, events.size() );
        assertEquals( 1, events.get( 0 ).path( "seq" ).asLong() );
        assertEquals( 2, events.get( 1 ).path( "seq" ).asLong() );
        assertEquals( 2, events.get( 1 ).path( "data" ).path( "n" ).asInt() );

        // a subscriber acked after two publishes is told seq=2, so its first event is 3
        final Session late = open( "s2", ALLOWED_ROLE );
        message( late, subscribeFrame(), ALLOWED_ROLE );
        assertEquals( 2, lastSentTo( "s2" ).path( "seq" ).asLong() );
    }

    @Test
    void eventsForUnregisteredTopicsAreNotStamped()
    {
        hub.onEvent( topicEvent( Map.of() ) );
        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    @Test
    void publishResolvesUnderTheCallersKey()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        // OTHER has no topic of this name - the canonical name embeds the caller, so publishing
        // into someone else's topic is not forbidden, it is unaddressable
        assertThrows( IllegalArgumentException.class, () -> hub.publish( publishParams( OTHER, NAME ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.publish( publishParams( OWNER, "unregistered" ) ) );

        hub.publish( publishParams( OWNER, NAME, GenericValue.newObject().put( "k", "v" ).build() ) );

        final ArgumentCaptor<Event> events = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( events.capture() );
        assertEquals( "admin.topic", events.getValue().getType() );
        assertTrue( events.getValue().isDistributed() );
        assertEquals( TOPIC, events.getValue().getData().get( "name" ) );
    }

    @Test
    void equalLocalNamesOfDifferentApplicationsAreDifferentTopics()
    {
        assertEquals( OWNER + ":" + NAME, hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
        assertEquals( OTHER + ":" + NAME, hub.setTopic( setParams( OTHER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) ) );

        final Session ours = open( "s1", ALLOWED_ROLE );
        message( ours, subscribeFrame(), ALLOWED_ROLE );
        final Session theirs = open( "s2", ALLOWED_ROLE );
        message( theirs, "{\"type\":\"subscribe\",\"topic\":\"" + OTHER + ":" + NAME + "\"}", ALLOWED_ROLE );

        hub.onEvent( Event.create( "admin.topic" ).value( "name", OTHER + ":" + NAME ).value( "data", Map.of() ).build() );

        assertEquals( 1, eventsTo( "s2" ).size() );
        assertEquals( 0, eventsTo( "s1" ).size() );
    }

    @Test
    void reRegistrationRevokesSubscribersFailingNewAllow()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        assertEquals( "ack", lastSentTo( "s1" ).path( "type" ).asText() );

        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( PrincipalKey.ofRole( "another.role" ) ) ) );

        final JsonNode deny = lastSentTo( "s1" );
        assertEquals( "deny", deny.path( "type" ).asText() );
        assertEquals( "forbidden", deny.path( "reason" ).asText() );

        hub.onEvent( topicEvent( Map.of() ) );
        assertEquals( 0, eventsTo( "s1" ).size() );
    }

    @Test
    void applicationStopClearsOwnershipButKeepsSequence()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        hub.onEvent( topicEvent( Map.of() ) );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( OWNER );
        topicRegistry.removeApplication( application );

        assertThrows( IllegalArgumentException.class, () -> hub.publish( publishParams( OWNER, NAME ) ) );

        // re-registration continues the numbering: gap counting survives the redeploy
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        hub.onEvent( topicEvent( Map.of() ) );

        final List<JsonNode> events = eventsTo( "s1" );
        assertEquals( 2, events.size() );
        assertEquals( 2, events.get( 1 ).path( "seq" ).asLong() );
    }

    @Test
    void inboundRequiresAckedSubscription()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

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
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        hub.onEvent( topicEvent( Map.of() ) );
        hub.onEvent( topicEvent( Map.of() ) );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        final JsonNode reAck = lastSentTo( "s1" );
        assertEquals( "ack", reAck.path( "type" ).asText() );
        // the re-ack reports the sequence stamped so far, so the next event is the one after it
        assertEquals( 2, reAck.path( "seq" ).asLong() );

        // subscribing twice does not deliver twice
        hub.onEvent( topicEvent( Map.of() ) );
        assertEquals( 3, eventsTo( "s1" ).size() );
    }

    @Test
    void unsubscribeStopsDelivery()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        message( session, "{\"type\":\"unsubscribe\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        hub.onEvent( topicEvent( Map.of() ) );
        assertEquals( 0, eventsTo( "s1" ).size() );
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
        throws Exception
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        for ( int i = 0; i < 35; i++ )
        {
            message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );
        }

        // 30 accepted pubs became inbound events; the rest were rejected with an error frame
        verify( eventPublisher, times( 30 ) ).publish( any() );
        final JsonNode error = lastSentTo( "s1" );
        assertEquals( "rateLimit", error.path( "code" ).asText() );
        assertTrue( error.path( "retryAfter" ).asLong() > 0 );
        // a single burst costs one violation, so the socket stays open
        verify( session, never() ).close( any( CloseReason.class ) );
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
        assertThrows( NullPointerException.class, () -> setParams( OWNER, null, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.setTopic( setParams( OWNER, "  ", PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.setTopic( setParams( OWNER, "with space", PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.setTopic( setParams( OWNER, "with:colon", PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.setTopic( setParams( OWNER, "x".repeat( 256 ), PrincipalKeys.from( ALLOWED_ROLE ) ) ) );
    }

    @Test
    void handleUpgradesWebSocketRequests()
    {
        final WebRequest request = new WebRequest();
        request.setRawPath( "/path/_/admin:events" );
        request.setWebSocketContext( mock( WebSocketContext.class ) );

        final WebResponse response = hub.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( List.of( "json" ), response.getWebSocket().getSubProtocols() );
    }

    @Test
    void handleRejectsSubPathsAndPlainRequests()
    {
        final WebRequest subPath = new WebRequest();
        subPath.setRawPath( "/path/_/admin:events/extra" );
        assertEquals( HttpStatus.NOT_FOUND, hub.handle( subPath ).getStatus() );

        final WebRequest plain = new WebRequest();
        plain.setRawPath( "/path/_/admin:events" );
        assertEquals( HttpStatus.BAD_REQUEST, hub.handle( plain ).getStatus() );
    }

    @Test
    void framesWithoutSessionAreIgnored()
    {
        final Session session = mock( Session.class );
        when( session.getId() ).thenReturn( "ghost" );
        socketEvent( WebSocketEvent.create().type( WebSocketEventType.MESSAGE ).session( session ).message( "{}" ), ALLOWED_ROLE );

        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    @Test
    void closeForgetsTheSession()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        socketEvent( WebSocketEvent.create().type( WebSocketEventType.CLOSE ).session( session ), ALLOWED_ROLE );

        message( session, "{\"type\":\"ping\"}", ALLOWED_ROLE );
        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    @Test
    void closeStopsDelivery()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        socketEvent( WebSocketEvent.create().type( WebSocketEventType.CLOSE ).session( session ), ALLOWED_ROLE );
        hub.onEvent( topicEvent( Map.of() ) );

        assertEquals( 0, eventsTo( "s1" ).size() );
    }

    @Test
    void registrationsAndNumberingOutliveTheSocketHalf()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        final String epoch = lastSentTo( "s1" ).path( "epoch" ).asText();
        hub.onEvent( topicEvent( Map.of() ) );

        // the socket-facing half restarts; the registry it references does not
        hub.deactivate();
        hub = new AdminEventHubImpl( webSocketManager, eventPublisher, topicRegistry );

        final Session reconnected = open( "s2", ALLOWED_ROLE );
        message( reconnected, subscribeFrame(), ALLOWED_ROLE );

        final JsonNode ack = lastSentTo( "s2" );
        assertEquals( "ack", ack.path( "type" ).asText() );
        // the registration is still there, the numbering continues, and the epoch holds, so a
        // subscriber counts gaps across the restart
        assertEquals( 1, ack.path( "seq" ).asLong() );
        assertEquals( epoch, ack.path( "epoch" ).asText() );

        hub.onEvent( topicEvent( Map.of() ) );
        assertEquals( 2, lastSentTo( "s2" ).path( "seq" ).asLong() );
        // the sockets of the previous instance are gone from delivery
        assertEquals( 1, eventsTo( "s1" ).size() );
    }

    @Test
    void blankTopicsAreBadFrames()
    {
        open( "s1", ALLOWED_ROLE );
        final Session session = sessionOf( "s1" );

        message( session, "{\"type\":\"subscribe\"}", ALLOWED_ROLE );
        message( session, "{\"type\":\"unsubscribe\",\"topic\":\" \"}", ALLOWED_ROLE );
        message( session, "{\"type\":\"pub\"}", ALLOWED_ROLE );

        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, times( 3 ) ).send( eq( "s1" ), frames.capture() );
        frames.getAllValues().forEach( frame -> assertEquals( "badFrame", parse( frame ).path( "code" ).asText() ) );
    }

    @Test
    void unsubscribeWithoutSubscriptionIsANoop()
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, "{\"type\":\"unsubscribe\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    @Test
    void repeatedViolationsCloseTheSocket()
        throws Exception
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        for ( int window = 0; window < 5; window++ )
        {
            exceedTheCap( session );
        }

        verify( session ).close( any( CloseReason.class ) );
    }

    @Test
    void aWindowInsideTheCapClearsTheViolations()
        throws Exception
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        for ( int window = 0; window < 4; window++ )
        {
            exceedTheCap( session );
        }

        // one window inside the cap, and the next window starts the count over
        nextWindow();
        message( session, pubFrame(), ALLOWED_ROLE );
        for ( int window = 0; window < 4; window++ )
        {
            exceedTheCap( session );
        }

        verify( session, never() ).close( any( CloseReason.class ) );
    }

    @Test
    void subscriptionsPerSocketAreCapped()
    {
        for ( int i = 0; i < 65; i++ )
        {
            hub.setTopic( setParams( OWNER, "topic" + i, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        }

        final Session session = open( "s1", RoleKeys.ADMIN );
        for ( int i = 0; i < 65; i++ )
        {
            message( session, "{\"type\":\"subscribe\",\"topic\":\"" + OWNER + ":topic" + i + "\"}", RoleKeys.ADMIN );
        }

        assertEquals( "subLimit", lastSentTo( "s1" ).path( "code" ).asText() );
        assertEquals( 64, framesTo( "s1" ).stream().filter( f -> "ack".equals( f.path( "type" ).asText() ) ).count() );
    }

    @Test
    void publishRejectsOversizedMessages()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        assertThrows( IllegalArgumentException.class, () -> hub.publish( publishParams( OWNER, NAME, GenericValue.newObject().put( "pad", "x".repeat( 100_001 ) ).build() ) ) );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void inboundToAnUnregisteredTopicIsAnError()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( OWNER );
        topicRegistry.removeApplication( application );

        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\"}", ALLOWED_ROLE );

        assertEquals( "unknown", lastSentTo( "s1" ).path( "code" ).asText() );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void onEventIgnoresForeignTypesAndMissingNames()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        hub.onEvent( Event.create( "node.updated" ).value( "name", TOPIC ).build() );
        hub.onEvent( Event.create( "admin.topic" ).value( "data", Map.of() ).build() );

        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    @Test
    void deactivateSurvivesCloseFailures()
        throws Exception
    {
        final Session session = open( "s1", ALLOWED_ROLE );
        doThrow( new IOException( "boom" ) ).when( session ).close( any( CloseReason.class ) );

        hub.deactivate();

        verify( session ).close( any( CloseReason.class ) );
    }

    @Test
    void subscribeAfterOwnerStopsIsDeniedRetryable()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( OWNER );
        topicRegistry.addApplication( application );
        topicRegistry.removeApplication( application );

        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        final JsonNode deny = lastSentTo( "s1" );
        assertEquals( "deny", deny.path( "type" ).asText() );
        assertEquals( "unknown", deny.path( "reason" ).asText() );
        assertEquals( 1, framesTo( "s1" ).size() );
    }

    @Test
    void publishWithoutMessageSendsEmptyObject()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );

        hub.publish( publishParams( OWNER, NAME ) );

        final ArgumentCaptor<Event> events = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( events.capture() );
        assertEquals( Map.of(), events.getValue().getData().get( "data" ) );
    }

    @Test
    void inboundNullDataIsAnEmptyObject()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\",\"data\":null}", ALLOWED_ROLE );

        final ArgumentCaptor<Event> events = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( events.capture() );
        assertEquals( Map.of(), events.getValue().getData().get( "data" ) );
    }

    @Test
    void inboundNullInsideDataIsRejectedWithoutClosing()
        throws Exception
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\",\"data\":{\"a\":null}}", ALLOWED_ROLE );

        assertEquals( "badFrame", lastSentTo( "s1" ).path( "code" ).asText() );
        verify( eventPublisher, never() ).publish( any() );
        verify( session, never() ).close( any( CloseReason.class ) );
    }

    @Test
    void inboundDataMustBeRepresentable()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );

        message( session, "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\",\"data\":{\"n\":123456789012345678901234567890}}",
                 ALLOWED_ROLE );

        assertEquals( "badFrame", lastSentTo( "s1" ).path( "code" ).asText() );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void violationCloseFailuresAreSwallowed()
        throws Exception
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        doThrow( new IOException( "boom" ) ).when( session ).close( any( CloseReason.class ) );

        for ( int window = 0; window < 5; window++ )
        {
            exceedTheCap( session );
        }

        verify( session ).close( any( CloseReason.class ) );
    }

    @Test
    void allowIsRequired()
    {
        assertThrows( NullPointerException.class, () -> SetTopicParams.create().owner( OWNER ).name( NAME ).build() );
    }

    @Test
    void emptyAllowClearsTheTopic()
    {
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        final Session session = open( "s1", ALLOWED_ROLE );
        message( session, subscribeFrame(), ALLOWED_ROLE );
        hub.onEvent( topicEvent( Map.of() ) );

        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.empty() ) );

        // clearing is not revocation: the subscriber is neither denied nor dropped
        assertTrue( framesTo( "s1" ).stream().noneMatch( f -> "deny".equals( f.path( "type" ).asText() ) ) );
        assertThrows( IllegalArgumentException.class, () -> hub.publish( publishParams( OWNER, NAME ) ) );
        final Session late = open( "s2", ALLOWED_ROLE );
        message( late, subscribeFrame(), ALLOWED_ROLE );
        assertEquals( "unknown", lastSentTo( "s2" ).path( "reason" ).asText() );

        // setting the topic again continues the numbering
        hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.from( ALLOWED_ROLE ) ) );
        hub.onEvent( topicEvent( Map.of() ) );
        final List<JsonNode> events = eventsTo( "s1" );
        assertEquals( 2, events.size() );
        assertEquals( 2, events.get( 1 ).path( "seq" ).asLong() );
    }

    @Test
    void emptyAllowForAnUnknownTopicIsANoop()
    {
        assertEquals( TOPIC, hub.setTopic( setParams( OWNER, NAME, PrincipalKeys.empty() ) ) );

        hub.onEvent( topicEvent( Map.of() ) );
        verify( webSocketManager, never() ).send( anyString(), anyString() );
    }

    private static SetTopicParams setParams( final ApplicationKey owner, final String name, final PrincipalKeys allow )
    {
        return SetTopicParams.create().owner( owner ).name( name ).allow( allow ).build();
    }

    private static PublishMessageParams publishParams( final ApplicationKey caller, final String name )
    {
        return PublishMessageParams.create().caller( caller ).name( name ).build();
    }

    private static PublishMessageParams publishParams( final ApplicationKey caller, final String name, final GenericValue message )
    {
        return PublishMessageParams.create().caller( caller ).name( name ).message( message ).build();
    }

    private Session sessionOf( final String id )
    {
        final Session session = mock( Session.class );
        when( session.getId() ).thenReturn( id );
        return session;
    }

    private void exceedTheCap( final Session session )
    {
        nextWindow();
        for ( int i = 0; i < 31; i++ )
        {
            message( session, pubFrame(), ALLOWED_ROLE );
        }
    }

    private void nextWindow()
    {
        clock += 11_000_000_000L;
    }

    private String pubFrame()
    {
        return "{\"type\":\"pub\",\"topic\":\"" + TOPIC + "\"}";
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

    private List<JsonNode> framesTo( final String id )
    {
        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, atLeastOnce() ).send( eq( id ), frames.capture() );
        return frames.getAllValues().stream().map( AdminEventHubImplTest::parse ).toList();
    }

    private List<JsonNode> eventsTo( final String id )
    {
        final ArgumentCaptor<String> frames = ArgumentCaptor.forClass( String.class );
        verify( webSocketManager, atLeastOnce() ).send( eq( id ), frames.capture() );
        return frames.getAllValues()
            .stream()
            .map( AdminEventHubImplTest::parse )
            .filter( f -> "event".equals( f.path( "type" ).asText() ) )
            .toList();
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
