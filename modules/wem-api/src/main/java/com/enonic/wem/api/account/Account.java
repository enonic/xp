package com.enonic.wem.api.account;

import org.joda.time.DateTime;

public abstract class Account<T extends Account>
{
    private final AccountKey key;

    private String displayName;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private boolean deleted;

    private boolean editable;

    protected Account( final AccountKey key )
    {
        this.key = key;
    }

    public final AccountKey getKey()
    {
        return this.key;
    }

    public final String getDisplayName()
    {
        if ( this.displayName == null )
        {
            return this.key.getLocalName();
        }

        return this.displayName;
    }

    public final DateTime getCreatedTime()
    {
        return this.createdTime;
    }

    public final DateTime getModifiedTime()
    {
        return this.modifiedTime;
    }

    public final boolean isDeleted()
    {
        return this.deleted;
    }

    public final boolean isEditable()
    {
        return this.editable;
    }

    public final T displayName( final String displayName )
    {
        this.displayName = displayName;
        return getThis();
    }

    public final T createdTime( final DateTime createdTime )
    {
        this.createdTime = createdTime;
        return getThis();
    }

    public final T modifiedTime( final DateTime modifiedTime )
    {
        this.modifiedTime = modifiedTime;
        return getThis();
    }

    public final T deleted( final boolean deleted )
    {
        this.deleted = deleted;
        return getThis();
    }

    public final T editable( final boolean editable )
    {
        this.editable = editable;
        return getThis();
    }

    protected final void copyTo( final Account target )
    {
        target.displayName = this.displayName;
        target.createdTime = this.createdTime;
        target.modifiedTime = this.modifiedTime;
        target.deleted = this.deleted;
        target.editable = this.editable;
    }

    @SuppressWarnings("unchecked")
    protected final T getThis()
    {
        return (T) this;
    }

    public abstract T copy();

    protected final boolean equals( final Account other )
    {
        return other.key.equals( this.key );
    }

    public final int hashCode()
    {
        return this.key.hashCode();
    }
}
