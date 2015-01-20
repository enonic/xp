package com.enonic.wem.api.content;

public class CompareStatus
{
    public enum Status
    {
        NEW,
        NEW_TARGET,
        NEWER,
        OLDER,
        DELETED,
        DELETED_TARGET,
        EQUAL,
        MOVED
    }

    private final Status status;

    public CompareStatus( final Status status )
    {
        this.status = status;
    }

    public Status getStatus()
    {
        return status;
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

        if ( status != that.status )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return status != null ? status.hashCode() : 0;
    }
}
