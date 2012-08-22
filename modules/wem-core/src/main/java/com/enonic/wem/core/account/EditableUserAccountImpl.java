package com.enonic.wem.core.account;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.editor.EditableUserAccount;

final class EditableUserAccountImpl
    extends EditableAccountImpl
    implements EditableUserAccount
{
    private String email;

    private byte[] photo;

    private DateTime lastLoginTime;

    @Override
    public String getEmail()
    {
        return this.email;
    }

    @Override
    public byte[] getPhoto()
    {
        return this.photo;
    }

    @Override
    public DateTime getLastLoginTime()
    {
        return this.lastLoginTime;
    }

    @Override
    public void setEmail( final String value )
    {
        setModified();
        this.email = value;
    }

    @Override
    public void setPhoto( final byte[] value )
    {
        setModified();
        this.photo = value;
    }

    public void setLastLoginTime( final DateTime lastLoginTime )
    {
        this.lastLoginTime = lastLoginTime;
    }
}
