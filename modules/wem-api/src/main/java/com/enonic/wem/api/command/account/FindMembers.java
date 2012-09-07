package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Command;

public final class FindMembers
    extends Command<AccountKeys>
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

        final FindMembers that = (FindMembers) o;

        if ( includeTransitive != that.includeTransitive )
        {
            return false;
        }
        if ( key != null ? !key.equals( that.key ) : that.key != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ( includeTransitive ? 1 : 0 );
        return result;
    }
}
