package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;

public final class DeleteAccount
    extends Command<Boolean>
{
    private AccountKey key;

    public AccountKey getKey()
    {
        return this.key;
    }

    public DeleteAccount key( final AccountKey key )
    {
        this.key = key;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.key, "Account key cannot be null" );
    }
}
