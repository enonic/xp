package com.enonic.wem.api.account;

import org.joda.time.DateTime;

public final class UserAccount
    extends Account<UserAccount>
{
    private String email;

    private byte[] photo;

    private DateTime lastLoginTime;

    private UserAccount( final AccountKey key )
    {
        super( key );
    }

    public String getEmail()
    {
        return this.email;
    }

    public byte[] getPhoto()
    {
        return this.photo;
    }

    public DateTime getLastLoginTime()
    {
        return this.lastLoginTime;
    }

    public UserAccount email( final String email )
    {
        this.email = email;
        return this;
    }

    public UserAccount photo( final byte[] photo )
    {
        this.photo = photo;
        return this;
    }

    public UserAccount lastLoginTime( final DateTime lastLoginTime )
    {
        this.lastLoginTime = lastLoginTime;
        return this;
    }

    private void copyTo( final UserAccount target )
    {
        super.copyTo( target );
        target.email = this.email;
        target.photo = this.photo;
        target.lastLoginTime = this.lastLoginTime;
    }

    @Override
    public UserAccount copy()
    {
        final UserAccount target = new UserAccount( getKey() );
        copyTo( target );
        return target;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof UserAccount ) && equals( (UserAccount) o );
    }

    public static UserAccount create( final String qName )
    {
        return new UserAccount( AccountKey.user( qName ) );
    }
}
