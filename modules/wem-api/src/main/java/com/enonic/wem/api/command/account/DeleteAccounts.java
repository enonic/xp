package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.Command;

public final class DeleteAccounts
    extends Command<Integer>
{
    private AccountKeys keys;

    public AccountKeys getKeys()
    {
        return this.keys;
    }

    public DeleteAccounts keys( final AccountKeys keys )
    {
        this.keys = keys;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.keys, "Account keys cannot be null" );
    }
}
