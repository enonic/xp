package com.enonic.xp.content;

public class CompareStatus
{
    public enum Status
    {
        NEW,
        NEW_TARGET,
        NEWER,
        OLDER,
        PENDING_DELETE,
        PENDING_DELETE_TARGET,
        EQUAL,
        MOVED,
        CONFLICT_PATH_EXISTS,
        CONFLICT_VERSION_BRANCH_DIVERGS

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

    public boolean isConflict()
    {
        return this.status.equals( Status.CONFLICT_PATH_EXISTS ) || status.equals( Status.CONFLICT_VERSION_BRANCH_DIVERGS );
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
