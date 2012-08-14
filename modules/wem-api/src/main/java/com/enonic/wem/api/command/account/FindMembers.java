package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.command.Command;

public final class FindMembers
    extends Command<AccountKeySet>
{
    private AccountKey key;

    private boolean includeTransitive = false;

    public AccountKey getKey()
    {
        return this.key;
    }

    public boolean isIncludeTransitive()
    {
        return this.includeTransitive;
    }

    public FindMembers key( final AccountKey key )
    {
        this.key = key;
        return this;
    }

    public FindMembers includeTransitive()
    {
        this.includeTransitive = true;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.key, "Account key cannot be null" );
        Preconditions.checkArgument( !this.key.isUser(), "Account must be group or role" );
    }
}
