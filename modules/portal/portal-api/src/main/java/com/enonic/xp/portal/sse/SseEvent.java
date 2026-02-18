package com.enonic.xp.portal.sse;

import java.util.Map;

public final class SseEvent
{
    private final SseEventType type;

    private final String id;

    private final Map<String, String> data;

    private final Throwable error;

    private SseEvent( final Builder builder )
    {
        this.type = builder.type;
        this.id = builder.id;
        this.data = builder.data;
        this.error = builder.error;
    }

    public SseEventType getType()
    {
        return this.type;
    }

    public String getId()
    {
        return this.id;
    }

    public Map<String, String> getData()
    {
        return this.data;
    }

    public Throwable getError()
    {
        return this.error;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private SseEventType type;

        private String id;

        private Map<String, String> data;

        private Throwable error;

        private Builder()
        {
        }

        public Builder type( final SseEventType type )
        {
            this.type = type;
            return this;
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder data( final Map<String, String> data )
        {
            this.data = data;
            return this;
        }

        public Builder error( final Throwable error )
        {
            this.error = error;
            return this;
        }

        public SseEvent build()
        {
            return new SseEvent( this );
        }
    }
}
