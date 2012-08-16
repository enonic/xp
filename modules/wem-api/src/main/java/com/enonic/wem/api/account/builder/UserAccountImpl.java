package com.enonic.wem.api.account.builder;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserAccount;

final class UserAccountImpl
    extends AccountImpl
    implements UserAccount, UserAccountBuilder
{
    protected String email;

    protected byte[] photo;

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
        return null;
    }

    @Override
    public UserAccountBuilder email( final String value )
    {
        this.email = value;
        return this;
    }

    @Override
    public UserAccountBuilder photo( final byte[] value )
    {
        this.photo = value;
        return this;
    }

    @Override
    public UserAccount build()
    {
        return this;
    }

    @Override
    public UserAccountBuilder displayName( final String value )
    {
        this.displayName = value;
        return this;
    }
}
