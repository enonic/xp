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
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FindAccounts that = (FindAccounts) o;

        if ( includeImage != that.includeImage )
        {
            return false;
        }
        if ( includeMembers != that.includeMembers )
        {
            return false;
        }
        if ( selector != null ? !selector.equals( that.selector ) : that.selector != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = selector != null ? selector.hashCode() : 0;
        result = 31 * result + ( includeImage ? 1 : 0 );
        result = 31 * result + ( includeMembers ? 1 : 0 );
        return result;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Account selector cannot be null" );
    }
}
