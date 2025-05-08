package com.enonic.xp.content;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.data.Property;

public final class WorkflowInfo
{
    private static final WorkflowInfo IN_PROGRESS = WorkflowInfo.create().
        state( WorkflowState.IN_PROGRESS ).
        build();

    private static final WorkflowInfo READY = WorkflowInfo.create().
        state( WorkflowState.READY ).
        build();

    private final WorkflowState state;

    private final ImmutableMap<String, WorkflowCheckState> checks;

    private WorkflowInfo( Builder builder )
    {
        this.state = builder.state;
        this.checks = builder.checks;
    }

    public WorkflowState getState()
    {
        return state;
    }

    public Map<String, WorkflowCheckState> getChecks()
    {
        return checks;
    }

    public static WorkflowInfo inProgress()
    {
        return IN_PROGRESS;
    }

    public static WorkflowInfo ready()
    {
        return READY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkflowInfo ) )
        {
            return false;
        }

        final WorkflowInfo that = (WorkflowInfo) o;
        return Objects.equals( state, that.state ) && Objects.equals( checks, that.checks );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( state, checks );
    }

    public static final class Builder
    {
        private WorkflowState state;

        private ImmutableMap<String, WorkflowCheckState> checks;

        private Builder()
        {
            this.checks = ImmutableMap.of();
        }

        public Builder state( WorkflowState state )
        {
            this.state = state;
            return this;
        }

        public Builder state( String state )
        {
            if ( state != null )
            {
                this.state = WorkflowState.valueOf( state );
            }
            return this;
        }

        public Builder checks( ImmutableMap<String, WorkflowCheckState> checks )
        {
            this.checks = checks;
            return this;
        }

        public Builder checks( Map<String, WorkflowCheckState> checks )
        {
            this.checks = ImmutableMap.copyOf( checks );
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.state, "state cannot be null" );
            for ( Map.Entry<String, WorkflowCheckState> e : this.checks.entrySet() )
            {
                Property.checkName( e.getKey() );
                Preconditions.checkNotNull( e.getValue(), "workflow check state cannot be null" );
            }
        }

        public WorkflowInfo build()
        {
            this.validate();
            return new WorkflowInfo( this );
        }
    }
}
