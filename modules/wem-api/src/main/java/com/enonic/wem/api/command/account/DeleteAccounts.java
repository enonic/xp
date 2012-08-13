package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.Command;

public final class DeleteAccounts
    extends Command<Integer>
{
    private AccountSelector selector;

    public AccountSelector getSelector()
    {
        return this.selector;
    }

    public DeleteAccounts selector( final AccountSelector selector )
    {
        this.selector = selector;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Account selector cannot be null" );
    }
}
