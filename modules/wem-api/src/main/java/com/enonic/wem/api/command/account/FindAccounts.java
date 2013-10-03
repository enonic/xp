package com.enonic.wem.api.command.account;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.Command;

public final class FindAccounts
    extends Command<AccountQueryHits>
{
    private AccountQuery query;

    public AccountQuery getQuery()
    {
        return this.query;
    }

    public FindAccounts query( final AccountQuery query )
    {
        this.query = query;
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
        return Objects.equal( this.query, that.query );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.query );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.query, "Account query cannot be null" );
    }
}
