package com.enonic.wem.api.account;

import org.joda.time.DateTime;

public abstract class Account
{
    private final AccountKey key;

    private String displayName;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private boolean deleted;

    private boolean editable;

    public Account( final AccountKey key )
    {
        this.key = key;
    }

    public final AccountKey getKey()
    {
        return this.key;
    }

    public final String getDisplayName()
    {
        return this.displayName != null ? this.displayName : this.key.getQualifiedName();
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

    public final void setDisplayName( final String value )
    {
        this.displayName = value;
    }

    public final void setCreatedTime( final DateTime value )
    {
        this.createdTime = value;
    }

    public final void setModifiedTime( final DateTime value )
    {
        this.modifiedTime = value;
    }

    public final void setDeleted( final boolean value )
    {
        this.deleted = value;
    }

    public final void setEditable( final boolean value )
    {
        this.editable = value;
    }
}
