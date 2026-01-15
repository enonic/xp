package com.enonic.xp.content;

import java.util.Objects;

public final class WorkflowInfo
{
    private static final WorkflowInfo IN_PROGRESS = WorkflowInfo.create().state( WorkflowState.IN_PROGRESS ).build();

    private static final WorkflowInfo READY = WorkflowInfo.create().state( WorkflowState.READY ).build();

    private final WorkflowState state;

    private WorkflowInfo( Builder builder )
    {
        this.state = builder.state;
    }

    public WorkflowState getState()
    {
        return state;
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
        return this == o || o instanceof final WorkflowInfo that && Objects.equals( state, that.state );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( state );
    }

    public static final class Builder
    {
        private WorkflowState state;

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

        private void validate()
        {
            Objects.requireNonNull( this.state, "state is required" );
        }

        public WorkflowInfo build()
        {
            this.validate();
            return new WorkflowInfo( this );
        }
    }
}
