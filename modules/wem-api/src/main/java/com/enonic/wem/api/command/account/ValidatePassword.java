package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;

public final class ValidatePassword
    extends Command<Boolean>
{
    private AccountKey key;

    private String password;

    public AccountKey getKey()
    {
        return this.key;
    }

    public String getPassword()
    {
        return this.password;
    }

    public ValidatePassword key( final AccountKey key )
    {
        this.key = key;
        return this;
    }

    public ValidatePassword password( final String password )
    {
        this.password = password;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.key, "Account key cannot be null" );
        Preconditions.checkNotNull( this.password, "Password cannot be null" );
        Preconditions.checkArgument( this.key.isUser(), "Cannot validate password for non-user account" );
        Preconditions.checkArgument( !this.key.isAnonymous(), "Cannot validate password for anonymous" );
    }
}
