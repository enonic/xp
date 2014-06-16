package com.enonic.wem.core.workspace.diff;

public class DiffStatus
{
    public enum State
    {
        NEW,
        NEWER,
        OLDER,
        CONFLICT,
        DELETED,
        EQUAL
    }

    private final State state;

    public DiffStatus( final State state )
    {
        this.state = state;
    }

    public State getState()
    {
        return state;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DiffStatus ) )
        {
            return false;
        }

        final DiffStatus that = (DiffStatus) o;

        if ( state != that.state )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return state != null ? state.hashCode() : 0;
    }
}
