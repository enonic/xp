package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;

public final class CreateAccount
    extends Command<AccountKey>
{
    private Account account;

    public Account getAccount()
    {
        return this.account;
    }

    public CreateAccount account( final Account account )
    {
        this.account = account;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.account, "Account cannot be null" );
        Preconditions.checkState( !this.account.getKey().isRole(), "Account cannot be a role" );
    }
}
