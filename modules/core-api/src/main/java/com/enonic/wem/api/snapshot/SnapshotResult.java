package com.enonic.wem.api.snapshot;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class SnapshotResult
{
    private final Set<String> indices;

    private final State state;

    private final String reason;

    private final String name;

    public enum State
    {
        SUCCESS,
        FAILED,
        PARTIAL,
        IN_PROGRESS
    }

    private SnapshotResult( Builder builder )
    {
        indices = builder.indices;
        state = builder.state;
        reason = builder.reason;
        name = builder.name;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Set<String> indices = Sets.newHashSet();

        private State state;

        private String reason;

        private String name;

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

        public SnapshotResult build()
        {
            return new SnapshotResult( this );
        }
    }
}
