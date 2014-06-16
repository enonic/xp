package com.enonic.wem.api.entity;

public class CompareState
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

    public CompareState( final State state )
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
        if ( !( o instanceof CompareState ) )
        {
            return false;
        }

        final CompareState that = (CompareState) o;

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
