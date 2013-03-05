package com.enonic.wem.api.command;

import java.util.Objects;

public final class UpdateResult
{

    private static final UpdateResult NOT_UPDATED = new UpdateResult( true, false, null );

    private static final UpdateResult UPDATED = new UpdateResult( true, true, null );

    private final boolean success;

    private final boolean updated;

    private final String failureCause;

    private UpdateResult( final boolean success, final boolean updated, final String failureCause )
    {
        this.success = success;
        this.updated = updated;
        this.failureCause = failureCause;
    }

    public boolean successful()
    {
        return this.success;
    }

    public boolean failed()
    {
        return !this.success;
    }

    public boolean isUpdated()
    {
        return this.updated;
    }

    public String failureCause()
    {
        return this.failureCause;
    }

    public static UpdateResult failure( final String failureCause )
    {
        return new UpdateResult( false, false, failureCause );
    }

    public static UpdateResult updated()
    {
        return UPDATED;
    }

    public static UpdateResult notUpdated()
    {
        return NOT_UPDATED;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof UpdateResult ) )
        {
            return false;
        }
        final UpdateResult that = (UpdateResult) o;
        return Objects.equals( this.success, that.success ) &&
            Objects.equals( this.updated, that.updated ) &&
            Objects.equals( this.failureCause, that.failureCause );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( success, updated, failureCause );
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper( this ).
            add( "successful", this.success ).
            add( "updated", this.updated ).
            add( "failure", this.failureCause ).
            omitNullValues().
            toString();
    }
}
