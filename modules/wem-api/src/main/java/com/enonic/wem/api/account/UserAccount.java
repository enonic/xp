package com.enonic.wem.api.account;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.profile.UserProfile;

public final class UserAccount
    extends Account
{
    private String email;

    private byte[] image;

    private DateTime lastLoginTime;

    private UserProfile profile;

    private UserAccount( final AccountKey key )
    {
        super( key );
    }

    public String getEmail()
    {
        return this.email;
    }

    public byte[] getImage()
    {
        return this.image;
    }

    public DateTime getLastLoginTime()
    {
        return this.lastLoginTime;
    }

    public UserProfile getProfile()
    {
        return this.profile;
    }

    public void setEmail( final String value )
    {
        this.email = value;
        setDirtyFlag();
    }

    public void setImage( final byte[] value )
    {
        this.image = value;
        setDirtyFlag();
    }

    public void setLastLoginTime( final DateTime value )
    {
        this.lastLoginTime = value;
    }

    public void setProfile( final UserProfile profile )
    {
        this.profile = profile;
        setDirtyFlag();
    }

    public static UserAccount create( final String qName )
    {
        return create( AccountKey.user( qName ) );
    }

    public static UserAccount create( final AccountKey key )
    {
        Preconditions.checkArgument( key.isUser(), "Account key must be of type user" );
        return new UserAccount( key );
    }
}
