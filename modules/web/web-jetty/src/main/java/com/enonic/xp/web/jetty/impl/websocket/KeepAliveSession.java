package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Forwarding {@link Session} wrapper used when {@code sessionAccess} is enabled. It delegates every operation
 * to the real session, but interposes on the message handlers the application registers so that
 * <strong>inbound</strong> messages refresh the HTTP session through its {@link HttpSession.Accessor}.
 * Server-to-client pushes never count as user activity.
 * <p>
 * The refresh is throttled to at most once per {@code sessionAccessThrottle}. If the session is already gone
 * ({@link IllegalStateException} from {@code access}) the socket is closed with code 1008 as a lazy backstop
 * to the {@link WebSocketSessionTracker} listener.
 */
final class KeepAliveSession
    implements Session
{
    private static final Logger LOG = LoggerFactory.getLogger( KeepAliveSession.class );

    private final Session delegate;

    private final HttpSession.Accessor sessionAccessor;

    private final long throttleNanos;

    private final AtomicLong lastTouchNanos;

    KeepAliveSession( final Session delegate, final HttpSession.Accessor sessionAccessor, final Duration sessionAccessThrottle )
    {
        this.delegate = delegate;
        this.sessionAccessor = sessionAccessor;
        this.throttleNanos = Math.max( 0, sessionAccessThrottle.toNanos() );
        this.lastTouchNanos = new AtomicLong( System.nanoTime() - this.throttleNanos );
    }

    private void touch()
    {
        final long now = System.nanoTime();
        final long last = this.lastTouchNanos.get();
        if ( now - last < this.throttleNanos || !this.lastTouchNanos.compareAndSet( last, now ) )
        {
            return;
        }

        try
        {
            this.sessionAccessor.access( session -> {
            } );
        }
        catch ( IllegalStateException e )
        {
            // The HTTP session is already gone; close the socket as a backstop (the listener normally wins).
            try
            {
                this.delegate.close( new CloseReason( CloseReason.CloseCodes.VIOLATED_POLICY, "HTTP session ended" ) );
            }
            catch ( IOException ex )
            {
                LOG.debug( "Failed to close WebSocket session [{}] after HTTP session was gone", this.delegate.getId(), ex );
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addMessageHandler( final MessageHandler handler )
    {
        if ( handler instanceof MessageHandler.Whole )
        {
            final Class<?> type = resolveMessageType( handler, MessageHandler.Whole.class );
            if ( type != null )
            {
                this.delegate.addMessageHandler( (Class<Object>) type, touching( (MessageHandler.Whole<Object>) handler ) );
                return;
            }
        }
        else if ( handler instanceof MessageHandler.Partial )
        {
            final Class<?> type = resolveMessageType( handler, MessageHandler.Partial.class );
            if ( type != null )
            {
                this.delegate.addMessageHandler( (Class<Object>) type, touching( (MessageHandler.Partial<Object>) handler ) );
                return;
            }
        }
        // Unable to determine the message type (e.g. a lambda handler); forward unwrapped so the socket still
        // works, at the cost of keep-alive touches for this particular handler.
        this.delegate.addMessageHandler( handler );
    }

    @Override
    public <T> void addMessageHandler( final Class<T> clazz, final MessageHandler.Whole<T> handler )
    {
        this.delegate.addMessageHandler( clazz, touching( handler ) );
    }

    @Override
    public <T> void addMessageHandler( final Class<T> clazz, final MessageHandler.Partial<T> handler )
    {
        this.delegate.addMessageHandler( clazz, touching( handler ) );
    }

    private <T> MessageHandler.Whole<T> touching( final MessageHandler.Whole<T> handler )
    {
        return message -> {
            touch();
            handler.onMessage( message );
        };
    }

    private <T> MessageHandler.Partial<T> touching( final MessageHandler.Partial<T> handler )
    {
        return ( message, last ) -> {
            touch();
            handler.onMessage( message, last );
        };
    }

    private static Class<?> resolveMessageType( final MessageHandler handler, final Class<?> handlerInterface )
    {
        for ( Class<?> type = handler.getClass(); type != null; type = type.getSuperclass() )
        {
            for ( final Type genericInterface : type.getGenericInterfaces() )
            {
                if ( genericInterface instanceof ParameterizedType parameterized && parameterized.getRawType() == handlerInterface )
                {
                    final Type argument = parameterized.getActualTypeArguments()[0];
                    if ( argument instanceof Class<?> klass )
                    {
                        return klass;
                    }
                }
            }
        }
        return null;
    }

    // --- pure delegation below ---

    @Override
    public WebSocketContainer getContainer()
    {
        return this.delegate.getContainer();
    }

    @Override
    public Set<MessageHandler> getMessageHandlers()
    {
        return this.delegate.getMessageHandlers();
    }

    @Override
    public void removeMessageHandler( final MessageHandler handler )
    {
        this.delegate.removeMessageHandler( handler );
    }

    @Override
    public String getProtocolVersion()
    {
        return this.delegate.getProtocolVersion();
    }

    @Override
    public String getNegotiatedSubprotocol()
    {
        return this.delegate.getNegotiatedSubprotocol();
    }

    @Override
    public List<Extension> getNegotiatedExtensions()
    {
        return this.delegate.getNegotiatedExtensions();
    }

    @Override
    public boolean isSecure()
    {
        return this.delegate.isSecure();
    }

    @Override
    public boolean isOpen()
    {
        return this.delegate.isOpen();
    }

    @Override
    public long getMaxIdleTimeout()
    {
        return this.delegate.getMaxIdleTimeout();
    }

    @Override
    public void setMaxIdleTimeout( final long milliseconds )
    {
        this.delegate.setMaxIdleTimeout( milliseconds );
    }

    @Override
    public void setMaxBinaryMessageBufferSize( final int length )
    {
        this.delegate.setMaxBinaryMessageBufferSize( length );
    }

    @Override
    public int getMaxBinaryMessageBufferSize()
    {
        return this.delegate.getMaxBinaryMessageBufferSize();
    }

    @Override
    public void setMaxTextMessageBufferSize( final int length )
    {
        this.delegate.setMaxTextMessageBufferSize( length );
    }

    @Override
    public int getMaxTextMessageBufferSize()
    {
        return this.delegate.getMaxTextMessageBufferSize();
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote()
    {
        return this.delegate.getAsyncRemote();
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote()
    {
        return this.delegate.getBasicRemote();
    }

    @Override
    public String getId()
    {
        return this.delegate.getId();
    }

    @Override
    public void close()
        throws IOException
    {
        this.delegate.close();
    }

    @Override
    public void close( final CloseReason closeReason )
        throws IOException
    {
        this.delegate.close( closeReason );
    }

    @Override
    public URI getRequestURI()
    {
        return this.delegate.getRequestURI();
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap()
    {
        return this.delegate.getRequestParameterMap();
    }

    @Override
    public String getQueryString()
    {
        return this.delegate.getQueryString();
    }

    @Override
    public Map<String, String> getPathParameters()
    {
        return this.delegate.getPathParameters();
    }

    @Override
    public Map<String, Object> getUserProperties()
    {
        return this.delegate.getUserProperties();
    }

    @Override
    public Principal getUserPrincipal()
    {
        return this.delegate.getUserPrincipal();
    }

    @Override
    public Set<Session> getOpenSessions()
    {
        return this.delegate.getOpenSessions();
    }
}
