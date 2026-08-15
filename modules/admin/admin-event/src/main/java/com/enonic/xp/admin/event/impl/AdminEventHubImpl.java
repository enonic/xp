package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.admin.event.PublishMessageParams;
import com.enonic.xp.admin.event.SetTopicParams;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEvent;

/**
 * Admin events hub: the {@code admin:events} websocket API, the {@link AdminEventHub} service,
 * and the fan-out of distributed {@code admin.topic} events to subscribed local sockets.
 * <p>
 * Wire protocol, JSON text frames. Client: {@code subscribe {topic}}, {@code unsubscribe {topic}},
 * {@code pub {topic, data}}, {@code ping}. Server: {@code ack {topic, seq, epoch}},
 * {@code deny {topic, reason}}, {@code event {topic, seq, data}}, {@code error {code, topic?}},
 * {@code pong}. Topics are addressed by canonical name. Subscribe is checked against the topic's
 * {@code allow}, plus {@code role:system.admin}, with the principals captured at the websocket
 * handshake; {@code deny.reason} is {@code unknown} for an unregistered topic,
 * {@code forbidden} for a failed check. {@code pub}
 * requires an acknowledged subscription and is republished node-locally as an
 * {@code admin.topic.in.<topic>} event carrying the verified user and socket id.
 * <p>
 * Sequence numbers are per topic and per node, monotonic for the lifetime of this instance;
 * {@code epoch} identifies the instance. {@code ack.seq} is the last sequence stamped at
 * subscription time; the first delivered event carries a higher one.
 */
@Component(immediate = true, service = {AdminEventHub.class, UniversalApiHandler.class, EventListener.class}, property = {
    "key=" + AdminEventHubImpl.API_KEY, "allowedPrincipals=role:system.admin.login"})
public final class AdminEventHubImpl
    implements AdminEventHub, UniversalApiHandler, EventListener
{
    static final String API_KEY = "admin:events";

    static final String TOPIC_EVENT_TYPE = "admin.topic";

    static final String INBOUND_EVENT_TYPE_PREFIX = "admin.topic.in.";

    private static final String WS_PROTOCOL = "json";

    private static final String GROUP_PREFIX = "admin.event.topic/";

    private static final char QUALIFIER = ':';

    private static final int MAX_TOPIC_NAME_LENGTH = 255;

    private static final int MAX_SUBSCRIPTIONS_PER_SOCKET = 64;

    private static final int MAX_PUBLISH_JSON_CHARS = 100_000;

    private static final long INBOUND_WINDOW_NANOS = 10_000_000_000L;

    private static final int MAX_INBOUND_PER_WINDOW = 30;

    private static final int MAX_VIOLATIONS = 5;

    private static final Logger LOG = LoggerFactory.getLogger( AdminEventHubImpl.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final WebSocketManager webSocketManager;

    private final EventPublisher eventPublisher;

    // sequence numbers are comparable only within one hub instance; deactivate closes every
    // socket, so a connection never observes two epochs
    private final String epoch = UUID.randomUUID().toString();

    private final ConcurrentMap<String, TopicState> topics = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ClientSession> sessions = new ConcurrentHashMap<>();

    @Activate
    public AdminEventHubImpl( @Reference final WebSocketManager webSocketManager, @Reference final EventPublisher eventPublisher )
    {
        this.webSocketManager = webSocketManager;
        this.eventPublisher = eventPublisher;
    }

    @Deactivate
    public void deactivate()
    {
        this.sessions.values().forEach( clientSession -> quietClose( clientSession.session ) );
        this.sessions.clear();
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder = WebResponse.create();

        if ( !WebHandlerHelper.findApiPath( request, API_KEY ).isEmpty() )
        {
            return responseBuilder.status( HttpStatus.NOT_FOUND ).build();
        }
        if ( !request.isWebSocket() )
        {
            return responseBuilder.status( HttpStatus.BAD_REQUEST ).build();
        }

        final WebSocketConfig webSocketConfig = new WebSocketConfig();
        webSocketConfig.setSubProtocols( List.of( WS_PROTOCOL ) );
        // defaults kept: terminateOnSessionExit=true, sessionAccess=false

        return responseBuilder.webSocket( webSocketConfig ).build();
    }

    @Override
    public void onSocketEvent( final WebSocketEvent event )
    {
        switch ( event.getType() )
        {
            case OPEN -> onOpen( event );
            case MESSAGE -> onMessage( event );
            case CLOSE -> sessions.remove( event.getSession().getId() );
            // ERROR is not terminal in the websocket layer; close so teardown runs on CLOSE
            case ERROR -> quietClose( event.getSession() );
            default ->
            {
            }
        }
    }

    private void onOpen( final WebSocketEvent event )
    {
        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        sessions.put( event.getSession().getId(), new ClientSession( event.getSession(), principals ) );
    }

    private void onMessage( final WebSocketEvent event )
    {
        final String id = event.getSession().getId();
        final ClientSession clientSession = sessions.get( id );
        final String message = event.getMessage();
        if ( clientSession == null || message == null )
        {
            return;
        }

        final JsonNode frame;
        try
        {
            frame = MAPPER.readTree( message );
        }
        catch ( JsonProcessingException e )
        {
            send( id, errorFrame( "badFrame", null ) );
            return;
        }

        switch ( frame.path( "type" ).asText( "" ) )
        {
            case "subscribe" -> handleSubscribe( clientSession, id, frame );
            case "unsubscribe" -> handleUnsubscribe( clientSession, id, frame );
            case "pub" -> handleInbound( clientSession, id, frame );
            case "ping" -> send( id, pongFrame() );
            default -> send( id, errorFrame( "badFrame", null ) );
        }
    }

    private void handleSubscribe( final ClientSession clientSession, final String id, final JsonNode frame )
    {
        final String topic = frame.path( "topic" ).asText( null );
        if ( topic == null || topic.isBlank() )
        {
            send( id, errorFrame( "badFrame", null ) );
            return;
        }

        final TopicState state = topics.get( topic );

        if ( clientSession.topics.contains( topic ) )
        {
            // idempotent re-ack
            send( id, ackFrame( topic, state != null ? state.seq.get() : 0 ) );
            return;
        }

        if ( clientSession.topics.size() >= MAX_SUBSCRIPTIONS_PER_SOCKET )
        {
            send( id, errorFrame( "subLimit", topic ) );
            return;
        }

        if ( state == null )
        {
            send( id, denyFrame( topic, "unknown" ) );
            return;
        }

        synchronized ( state.lock )
        {
            // evaluated and joined under the topic lock: atomic against re-registration and
            // against stamp-and-send, so ack.seq is exact
            final PrincipalKeys allow = state.allow;
            if ( allow == null )
            {
                send( id, denyFrame( topic, "unknown" ) );
                return;
            }
            if ( !isAllowed( clientSession.principals, allow ) )
            {
                send( id, denyFrame( topic, "forbidden" ) );
                return;
            }
            webSocketManager.addToGroup( group( topic ), id );
            clientSession.topics.add( topic );
            send( id, ackFrame( topic, state.seq.get() ) );
        }
    }

    private void handleUnsubscribe( final ClientSession clientSession, final String id, final JsonNode frame )
    {
        final String topic = frame.path( "topic" ).asText( null );
        if ( topic == null || topic.isBlank() )
        {
            send( id, errorFrame( "badFrame", null ) );
            return;
        }
        if ( clientSession.topics.remove( topic ) )
        {
            webSocketManager.removeFromGroup( group( topic ), id );
        }
    }

    private void handleInbound( final ClientSession clientSession, final String id, final JsonNode frame )
    {
        final String topic = frame.path( "topic" ).asText( null );
        if ( topic == null || topic.isBlank() )
        {
            send( id, errorFrame( "badFrame", null ) );
            return;
        }
        if ( !allowInbound( clientSession ) )
        {
            send( id, errorFrame( "rateLimit", topic ) );
            return;
        }
        if ( !clientSession.topics.contains( topic ) )
        {
            send( id, errorFrame( "notSubscribed", topic ) );
            return;
        }
        final TopicState state = topics.get( topic );
        if ( state == null || state.allow == null )
        {
            send( id, errorFrame( "unknown", topic ) );
            return;
        }

        final GenericValue data;
        try
        {
            data = inboundData( frame.get( "data" ) );
        }
        catch ( IllegalArgumentException e )
        {
            send( id, errorFrame( "badFrame", topic ) );
            return;
        }

        // identity values are hub-set; client-supplied ones are ignored
        eventPublisher.publish( Event.create( INBOUND_EVENT_TYPE_PREFIX + topic )
                                    .distributed( false )
                                    .value( "topic", topic )
                                    .value( "data", data.toRawJava() )
                                    .value( "user", currentUserKey() )
                                    .value( "socketId", id )
                                    .build() );
    }

    private static GenericValue inboundData( final JsonNode dataNode )
    {
        return dataNode == null || dataNode.isNull()
            ? GenericValue.newObject().build()
            : GenericValue.fromRawJava( MAPPER.convertValue( dataNode, Object.class ) );
    }

    private static String currentUserKey()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.getUser() != null ? authInfo.getUser().getKey().toString() : null;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( !TOPIC_EVENT_TYPE.equals( event.getType() ) )
        {
            return;
        }
        if ( !( event.getData().get( "name" ) instanceof String name ) )
        {
            return;
        }
        final TopicState state = topics.get( name );
        if ( state == null || state.allow == null )
        {
            return;
        }

        synchronized ( state.lock )
        {
            if ( state.allow == null )
            {
                return;
            }
            // stamped regardless of local subscribers
            final long seq = state.seq.incrementAndGet();
            webSocketManager.sendToGroup( group( name ), eventFrame( name, seq, event.getData().get( "data" ) ) );
        }
    }

    @Override
    public String setTopic( final SetTopicParams params )
    {
        final String topic = qualify( params.getOwner(), params.getName() );
        final PrincipalKeys allow = params.getAllow();

        if ( allow.isEmpty() )
        {
            // clears the registration like an application stop: no revocation, memberships and
            // the sequence counter persist, delivery resumes on the next non-empty set
            final TopicState state = topics.get( topic );
            if ( state != null )
            {
                synchronized ( state.lock )
                {
                    state.allow = null;
                }
            }
            return topic;
        }

        final TopicState state = topics.computeIfAbsent( topic, key -> new TopicState() );
        synchronized ( state.lock )
        {
            state.allow = allow;

            // re-evaluate current subscribers against the new allow
            sessions.forEach( ( id, clientSession ) -> {
                if ( clientSession.topics.contains( topic ) && !isAllowed( clientSession.principals, allow ) )
                {
                    clientSession.topics.remove( topic );
                    webSocketManager.removeFromGroup( group( topic ), id );
                    send( id, denyFrame( topic, "forbidden" ) );
                }
            } );
        }
        return topic;
    }

    @Override
    public void publish( final PublishMessageParams params )
    {
        final String name = params.getName();
        final String topic = qualify( params.getCaller(), name );
        final TopicState state = topics.get( topic );
        if ( state == null || state.allow == null )
        {
            throw new IllegalArgumentException( "Topic [" + name + "] is not registered by application [" + params.getCaller() + "]" );
        }

        final Object data = params.getMessage().toRawJava();
        if ( toJson( data ).length() > MAX_PUBLISH_JSON_CHARS )
        {
            throw new IllegalArgumentException( "Message for topic [" + name + "] exceeds " + MAX_PUBLISH_JSON_CHARS + " characters" );
        }

        eventPublisher.publish(
            Event.create( TOPIC_EVENT_TYPE ).distributed( true ).value( "name", topic ).value( "data", data ).build() );
    }

    private static String toJson( final Object data )
    {
        try
        {
            return MAPPER.writeValueAsString( data );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static String qualify( final ApplicationKey owner, final String name )
    {
        if ( name.isBlank() || name.length() > MAX_TOPIC_NAME_LENGTH || name.indexOf( QUALIFIER ) >= 0 ||
            name.chars().anyMatch( Character::isWhitespace ) )
        {
            throw new IllegalArgumentException( "Invalid topic name [" + name + "]" );
        }
        return owner + String.valueOf( QUALIFIER ) + name;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApplication( final Application application )
    {
        // tracked only for removeApplication
    }

    public void removeApplication( final Application application )
    {
        final String prefix = application.getKey() + String.valueOf( QUALIFIER );
        topics.forEach( ( topic, state ) -> {
            if ( topic.startsWith( prefix ) )
            {
                synchronized ( state.lock )
                {
                    // registration clears; the sequence counter is kept
                    state.allow = null;
                }
            }
        } );
    }

    private boolean allowInbound( final ClientSession clientSession )
    {
        synchronized ( clientSession )
        {
            final long now = System.nanoTime();
            if ( now - clientSession.inboundWindowStart > INBOUND_WINDOW_NANOS )
            {
                clientSession.inboundWindowStart = now;
                clientSession.inboundCount = 0;
            }
            if ( ++clientSession.inboundCount > MAX_INBOUND_PER_WINDOW )
            {
                registerViolation( clientSession );
                return false;
            }
            return true;
        }
    }

    private void registerViolation( final ClientSession clientSession )
    {
        synchronized ( clientSession )
        {
            if ( ++clientSession.violations >= MAX_VIOLATIONS )
            {
                try
                {
                    clientSession.session.close( new CloseReason( CloseReason.CloseCodes.VIOLATED_POLICY, "Too many messages" ) );
                }
                catch ( IOException | RuntimeException e )
                {
                    LOG.debug( "Failed to close misbehaving websocket session", e );
                }
            }
        }
    }

    private static boolean isAllowed( final PrincipalKeys principals, final PrincipalKeys allow )
    {
        return principals.contains( RoleKeys.ADMIN ) || allow.stream().anyMatch( principals::contains );
    }

    private void send( final String id, final String message )
    {
        webSocketManager.send( id, message );
    }

    private static void quietClose( final Session session )
    {
        try
        {
            session.close( new CloseReason( CloseReason.CloseCodes.GOING_AWAY, "Hub closing" ) );
        }
        catch ( IOException | RuntimeException e )
        {
            LOG.debug( "Failed to close websocket session", e );
        }
    }

    private static String group( final String topic )
    {
        return GROUP_PREFIX + topic;
    }

    private String ackFrame( final String topic, final long seq )
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put( "type", "ack" );
        node.put( "topic", topic );
        node.put( "seq", seq );
        node.put( "epoch", epoch );
        return node.toString();
    }

    private static String denyFrame( final String topic, final String reason )
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put( "type", "deny" );
        node.put( "topic", topic );
        node.put( "reason", reason );
        return node.toString();
    }

    private static String eventFrame( final String topic, final long seq, final Object data )
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put( "type", "event" );
        node.put( "topic", topic );
        node.put( "seq", seq );
        node.set( "data", MAPPER.valueToTree( data ) );
        return node.toString();
    }

    private static String errorFrame( final String code, final String topic )
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put( "type", "error" );
        node.put( "code", code );
        if ( topic != null )
        {
            node.put( "topic", topic );
        }
        return node.toString();
    }

    private static String pongFrame()
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put( "type", "pong" );
        return node.toString();
    }

    private static final class TopicState
    {
        final Object lock = new Object();

        final AtomicLong seq = new AtomicLong();

        // subscribe ACL; null while unregistered
        volatile PrincipalKeys allow;
    }

    private static final class ClientSession
    {
        final Session session;

        final PrincipalKeys principals;

        final Set<String> topics = ConcurrentHashMap.newKeySet();

        long inboundWindowStart;

        int inboundCount;

        int violations;

        ClientSession( final Session session, final PrincipalKeys principals )
        {
            this.session = session;
            this.principals = principals;
        }
    }
}
