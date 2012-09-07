package com.enonic.wem.api.command.account;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.command.Command;

public final class GetAccounts
    extends Command<Accounts>
{
    private AccountKeys keys;

    private boolean includeImage;

    private boolean includeMembers;

    private boolean includeProfile;

    public AccountKeys getKeys()
    {
        return this.keys;
    }

    public boolean isIncludeImage()
    {
        return this.includeImage;
    }

    public boolean isIncludeMembers()
    {
        return this.includeMembers;
    }

    public boolean isIncludeProfile()
    {
        return this.includeProfile;
    }

    public GetAccounts keys( final AccountKeys keys )
    {
        this.keys = keys;
        return this;
    }

    public GetAccounts includeImage()
    {
        this.includeImage = true;
        return this;
    }

    public GetAccounts includeMembers()
    {
        this.includeMembers = true;
        return this;
    }

    public GetAccounts includeProfile()
    {
        this.includeProfile = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetAccounts ) )
        {
            return false;
        }

        final GetAccounts that = (GetAccounts) o;
        return Objects.equal( this.keys, that.keys ) &&
            Objects.equal( this.includeImage, that.includeImage ) &&
            Objects.equal( this.includeMembers, that.includeMembers ) &&
            Objects.equal( this.includeProfile, that.includeProfile );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.keys, this.includeImage, this.includeMembers, this.includeProfile );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.keys, "Account keys cannot be null" );
    }
}
