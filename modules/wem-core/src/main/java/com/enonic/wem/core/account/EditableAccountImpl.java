package com.enonic.wem.core.account;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.editor.EditableAccount;

abstract class EditableAccountImpl
    implements EditableAccount
{
    private AccountKey key;

    private String displayName;

    private DateTime createdTime;

    private DateTime modifiedTime;

    private boolean deleted;

    private boolean editable;

    private boolean modified;

    @Override
    public final AccountKey getKey()
    {
        return this.key;
    }

    @Override
    public final String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public final DateTime getCreatedTime()
    {
        return this.createdTime;
    }

    @Override
    public final DateTime getModifiedTime()
    {
        return this.modifiedTime;
    }

    @Override
    public final boolean isDeleted()
    {
        return this.deleted;
    }

    @Override
    public final boolean isEditable()
    {
        return this.editable;
    }

    @Override
    public final void setDisplayName( final String value )
    {
        setModified();
        this.displayName = value;
    }

    public final void setKey( final AccountKey key )
    {
        this.key = key;
    }

    public final void setCreatedTime( final DateTime createdTime )
    {
        this.createdTime = createdTime;
    }

    public final void setModifiedTime( final DateTime modifiedTime )
    {
        this.modifiedTime = modifiedTime;
    }

    public final void setDeleted( final boolean deleted )
    {
        this.deleted = deleted;
    }

    public final void setEditable( final boolean editable )
    {
        this.editable = editable;
    }

    public boolean isModified()
    {
        return modified;
    }

    protected void setModified()
    {
        this.modified = true;
    }

    public void resetModified()
    {
        this.modified = false;
    }
}
