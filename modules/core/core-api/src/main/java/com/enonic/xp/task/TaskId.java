package com.enonic.xp.task;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class TaskId
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final String value;

    private TaskId( final String value )
    {
        Objects.requireNonNull( value, "TaskId cannot be null" );
        Preconditions.checkArgument( !value.isBlank(), "TaskId cannot be blank" );
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof TaskId && value.equals( ( ( (TaskId) o ).value ) );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
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
