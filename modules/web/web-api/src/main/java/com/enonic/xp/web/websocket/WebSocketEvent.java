package com.enonic.xp.web.websocket;

import java.util.Map;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

public final class WebSocketEvent
{
    private final WebSocketEventType type;

    private final Session session;

    private final Throwable error;

    private final CloseReason closeReason;

    private final String message;

    private final Map<String, String> data;

    private WebSocketEvent( final Builder builder )
    {
        this.type = builder.type;
        this.session = builder.session;
        this.error = builder.error;
        this.closeReason = builder.closeReason;
        this.message = builder.message;
        this.data = builder.data;
    }

    public WebSocketEventType getType()
    {
        return this.type;
    }

    public Session getSession()
    {
        return this.session;
    }

    public Throwable getError()
    {
        return this.error;
    }

    public CloseReason getCloseReason()
    {
        return this.closeReason;
    }

    public String getMessage()
    {
        return this.message;
    }

    public Map<String, String> getData()
    {
        return this.data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private WebSocketEventType type;

        private Session session;

        private Throwable error;

        private CloseReason closeReason;

        private String message;

        private Map<String, String> data;

        private Builder()
        {
        }

        public Builder type( final WebSocketEventType type )
        {
            this.type = type;
            return this;
        }

        public Builder session( final Session session )
        {
            this.session = session;
            return this;
        }

        public Builder error( final Throwable error )
        {
            this.error = error;
            return this;
        }

        public Builder closeReason( final CloseReason closeReason )
        {
            this.closeReason = closeReason;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder data( final Map<String, String> data )
        {
            this.data = data;
            return this;
        }

        public WebSocketEvent build()
        {
            return new WebSocketEvent( this );
        }
    }
}
