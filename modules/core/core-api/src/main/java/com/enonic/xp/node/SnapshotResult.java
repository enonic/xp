package com.enonic.xp.node;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class SnapshotResult
{
    private final Set<String> indices;

    private final State state;

    private final String reason;

    private final String name;

    private final Instant timestamp;

    public enum State
    {
        SUCCESS,
        FAILED,
        PARTIAL,
        IN_PROGRESS
    }

    private SnapshotResult( Builder builder )
    {
        this.indices = builder.indices;
        this.state = builder.state;
        this.reason = builder.reason;
        this.name = builder.name;
        this.timestamp = builder.timestamp;
    }

    public Set<String> getIndices()
    {
        return indices;
    }

    public State getState()
    {
        return state;
    }

    public String getReason()
    {
        return reason;
    }

    public String getName()
    {
        return name;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final Set<String> indices = new HashSet<>();

        private State state;

        private String reason;

        private String name;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder indices( final Collection<String> indices )
        {
            this.indices.addAll( indices );
            return this;
        }

        public Builder state( final State state )
        {
            this.state = state;
            return this;
        }

        public Builder reason( final String reason )
        {
            this.reason = reason;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public SnapshotResult build()
        {
            return new SnapshotResult( this );
        }
    }
}
