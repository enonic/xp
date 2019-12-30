package com.enonic.xp.task;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class TaskId
{
    private final String value;

    private TaskId( final String value )
    {
        Preconditions.checkNotNull( value, "TaskId cannot be null" );
        Preconditions.checkArgument( !value.trim().isEmpty(), "TaskId cannot be blank" );
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( !( o instanceof TaskId ) )
        {
            return false;
        }

        final TaskId other = (TaskId) o;
        return Objects.equals( value, other.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static TaskId from( String string )
    {
        return new TaskId( string );
    }
}
