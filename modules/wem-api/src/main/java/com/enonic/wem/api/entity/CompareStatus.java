package com.enonic.wem.api.entity;

public class CompareStatus
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

    public CompareStatus( final State state )
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
        if ( !( o instanceof CompareStatus ) )
        {
            return false;
        }

        final CompareStatus that = (CompareStatus) o;

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
