package com.enonic.wem.api.account;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.profile.UserProfile;

public final class UserAccount
    extends Account
{
    private String email;

    private byte[] image;

    private DateTime lastLoginTime;

    private UserProfile profile;

    private UserAccount( final UserKey key )
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
    }

    public void setImage( final byte[] value )
    {
        this.image = value;
    }

    public void setLastLoginTime( final DateTime value )
    {
        this.lastLoginTime = value;
    }

    public void setProfile( final UserProfile profile )
    {
        this.profile = profile;
    }

    public static UserAccount create( final String qName )
    {
        return create( UserKey.from( qName ) );
    }

    public static UserAccount create( final UserKey key )
    {
        return new UserAccount( key );
    }
}
