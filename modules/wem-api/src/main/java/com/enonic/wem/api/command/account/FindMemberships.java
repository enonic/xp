package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.command.Command;

public final class FindMemberships
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

    public FindMemberships key( final AccountKey key )
    {
        this.key = key;
        return this;
    }

    public FindMemberships includeTransitive()
    {
        this.includeTransitive = true;
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

        final FindMemberships that = (FindMemberships) o;

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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.key, "Account key cannot be null" );
    }
}
