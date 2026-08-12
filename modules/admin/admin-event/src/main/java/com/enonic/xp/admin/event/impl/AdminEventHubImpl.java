package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEvent;

/**
 * The admin events hub (<a href="https://github.com/enonic/xp/issues/12253">#12253</a>): one
 * websocket endpoint multiplexing app-registered topics, with the topic's {@code allow} enforced
 * at subscribe and a per-topic, per-node sequence stamped on every delivered message so
 * subscribers can count socket-leg loss.
 * <p>
 * Publishes travel as distributed {@code admin.topic} events, so each node's hub instance fans out
 * to its own sockets. Script publishers cannot forge them: lib-event prefixes everything
 * script-sent with {@code custom.}, and the only other way in - {@link AdminEventHub#publish} -
 * verifies topic ownership.
 * <p>
 * One instance serves every connection, so all per-connection state is keyed by session id.
 * Principals are captured when the socket opens (they are frozen at the upgrade handshake anyway)
 * so that re-registrations can re-evaluate subscribers without a socket context at hand.
 */
@Component(immediate = true, service = {AdminEventHub.class, UniversalApiHandler.class, EventListener.class}, property = {
    "key=" + AdminEventHubImpl.API_KEY, "allowedPrincipals=role:system.admin.login", "mount=web"})
public final class AdminEventHubImpl
    implements AdminEventHub, UniversalApiHandler, EventListener
{
    static final String API_KEY = "admin:events";

    static final String TOPIC_EVENT_TYPE = "admin.topic";

    static final String INBOUND_EVENT_TYPE_PREFIX = "admin.topic.in.";

    private static final String WS_PROTOCOL = "json";

    private static final String GROUP_PREFIX = "admin.event.topic/";

    private static final int MAX_TOPIC_NAME_LENGTH = 255;

    private static final int MAX_SUBSCRIPTIONS_PER_SOCKET = 64;

    private static final int MAX_INBOUND_FRAME_CHARS = 65_536;

    private static final int MAX_PUBLISH_JSON_CHARS = 100_000;

    private static final long INBOUND_WINDOW_NANOS = 10_000_000_000L;

    private static final int MAX_INBOUND_PER_WINDOW = 30;

    private static final int MAX_VIOLATIONS = 5;

    private static final Logger LOG = LoggerFactory.getLogger( AdminEventHubImpl.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final WebSocketManager webSocketManager;

    private final EventPublisher eventPublisher;

    // one epoch per hub incarnation: sequence numbers are comparable within it and meaningless
    // across it, and deactivate closes every socket so an epoch cannot change mid-connection
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
        // memberships and counters of this incarnation die here; clients reconnect and see a new epoch
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
        // defaults are load-bearing: terminateOnSessionExit keeps logout closing the socket, and
        // sessionAccess stays off so client pings never extend the login

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
            // ERROR is not terminal in the websocket layer (and also fires on handler exceptions):
            // close, so the one teardown path - CLOSE - runs
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

        if ( message.length() > MAX_INBOUND_FRAME_CHARS )
        {
            send( id, errorFrame( "tooLarge", null ) );
            registerViolation( clientSession );
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
            // idempotent re-ack: doubles as a cheap resync probe for the client
            send( id, ackFrame( topic, state != null ? state.seq.get() : 0 ) );
            return;
        }

        if ( clientSession.topics.size() >= MAX_SUBSCRIPTIONS_PER_SOCKET )
        {
            send( id, errorFrame( "subLimit", topic ) );
            return;
        }

        final TopicRegistration registration = state != null ? state.registration : null;
        if ( registration == null )
        {
            // no server-side pending state: the client keeps its wanted-set and retries
            send( id, denyFrame( topic, "unknown" ) );
            return;
        }
        if ( !isAllowed( clientSession.principals, registration.allow() ) )
        {
            send( id, denyFrame( topic, "forbidden" ) );
            return;
        }

        synchronized ( state.lock )
        {
            // membership, counter read and ack are atomic against this topic's stamp-and-send,
            // so ack.seq is exact: the first delivered event is seq+1, any jump is countable loss
            if ( state.registration == null )
            {
                send( id, denyFrame( topic, "unknown" ) );
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
        // membership-is-authorization, reused: inbound needs the same acked subscription
        if ( !clientSession.topics.contains( topic ) )
        {
            send( id, errorFrame( "notSubscribed", topic ) );
            return;
        }
        final TopicState state = topics.get( topic );
        if ( state == null || state.registration == null )
        {
            send( id, errorFrame( "unknown", topic ) );
            return;
        }

        final JsonNode dataNode = frame.get( "data" );
        final Object data = dataNode == null || dataNode.isNull() ? Map.of() : MAPPER.convertValue( dataNode, Object.class );

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final String user = authInfo.getUser() != null ? authInfo.getUser().getKey().toString() : null;

        // node-local on purpose: the producer app instance on this node handles it, exactly like a
        // socket of its own would; identity fields are hub-set, never taken from the frame
        eventPublisher.publish( Event.create( INBOUND_EVENT_TYPE_PREFIX + topic )
                                    .distributed( false )
                                    .value( "topic", topic )
                                    .value( "data", data )
                                    .value( "user", user )
                                    .value( "socketId", id )
                                    .build() );
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
        if ( state == null || state.registration == null )
        {
            return;
        }

        synchronized ( state.lock )
        {
            // stamped whether or not anyone is subscribed on this node: ack.seq stays truthful
            final long seq = state.seq.incrementAndGet();
            webSocketManager.sendToGroup( group( name ), eventFrame( name, seq, event.getData().get( "data" ) ) );
        }
    }

    @Override
    public void registerTopic( final String name, final PrincipalKeys allow, final ApplicationKey owner )
    {
        Objects.requireNonNull( owner, "owner is required" );
        if ( name == null || name.isBlank() || name.length() > MAX_TOPIC_NAME_LENGTH || name.chars().anyMatch( Character::isWhitespace ) )
        {
            throw new IllegalArgumentException( "Invalid topic name [" + name + "]" );
        }
        final PrincipalKeys effectiveAllow = allow != null ? allow : PrincipalKeys.empty();

        final TopicState state = topics.computeIfAbsent( name, key -> new TopicState() );
        synchronized ( state.lock )
        {
            final TopicRegistration existing = state.registration;
            if ( existing != null && !existing.owner().equals( owner ) )
            {
                throw new IllegalArgumentException(
                    "Topic [" + name + "] is already registered by application [" + existing.owner() + "]" );
            }
            if ( existing == null && state.lastOwner != null && !state.lastOwner.equals( owner ) )
            {
                // names are convention-only, so a name changing hands is legal - but worth a trace
                LOG.warn( "Topic [{}] changes owner from [{}] to [{}]", name, state.lastOwner, owner );
            }
            state.registration = new TopicRegistration( owner, effectiveAllow );
            state.lastOwner = owner;

            // re-evaluation doubles as revocation: subscribers failing the new allow get denied
            sessions.forEach( ( id, clientSession ) -> {
                if ( clientSession.topics.contains( name ) && !isAllowed( clientSession.principals, effectiveAllow ) )
                {
                    clientSession.topics.remove( name );
                    webSocketManager.removeFromGroup( group( name ), id );
                    send( id, denyFrame( name, "forbidden" ) );
                }
            } );
        }
    }

    @Override
    public void publish( final ApplicationKey caller, final String name, final Map<String, ?> message )
    {
        Objects.requireNonNull( caller, "caller is required" );
        final TopicState state = name != null ? topics.get( name ) : null;
        final TopicRegistration registration = state != null ? state.registration : null;
        if ( registration == null || !registration.owner().equals( caller ) )
        {
            throw new IllegalArgumentException( "Topic [" + name + "] is not registered by application [" + caller + "]" );
        }

        final Map<String, ?> data = message != null ? message : Map.of();
        try
        {
            if ( MAPPER.writeValueAsString( data ).length() > MAX_PUBLISH_JSON_CHARS )
            {
                throw new IllegalArgumentException( "Message for topic [" + name + "] exceeds " + MAX_PUBLISH_JSON_CHARS + " characters" );
            }
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalArgumentException( "Message for topic [" + name + "] is not serializable", e );
        }

        eventPublisher.publish(
            Event.create( TOPIC_EVENT_TYPE ).distributed( true ).value( "name", name ).value( "data", data ).build() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApplication( final Application application )
    {
        // registrations arrive through registerTopic; tracking exists for the teardown below
    }

    public void removeApplication( final Application application )
    {
        final ApplicationKey key = application.getKey();
        topics.forEach( ( name, state ) -> {
            synchronized ( state.lock )
            {
                if ( state.registration != null && state.registration.owner().equals( key ) )
                {
                    // ownership clears, the counter stays: sequence numbers never reset for the
                    // life of this hub instance, so a redeploy does not corrupt gap counting
                    state.registration = null;
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
        // the group registry is one flat namespace per node, shared with application code - the
        // prefix keeps hub topics out of anyone else's way
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

    private record TopicRegistration(ApplicationKey owner, PrincipalKeys allow)
    {
    }

    private static final class TopicState
    {
        final Object lock = new Object();

        final AtomicLong seq = new AtomicLong();

        volatile TopicRegistration registration;

        volatile ApplicationKey lastOwner;
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
