package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.Command;

public final class FindAccounts
    extends Command<AccountResult>
{
    private AccountSelector selector;

    private boolean includeImage;

    private boolean includeMembers;

    public AccountSelector getSelector()
    {
        return this.selector;
    }

    public boolean isIncludeImage()
    {
        return this.includeImage;
    }

    public boolean isIncludeMembers()
    {
        return this.includeMembers;
    }

    public FindAccounts selector( final AccountSelector selector )
    {
        this.selector = selector;
        return this;
    }

    public FindAccounts includeImage()
    {
        this.includeImage = true;
        return this;
    }

    public FindAccounts includeMembers()
    {
        this.includeMembers = true;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Account selector cannot be null" );
    }
}
