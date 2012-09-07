package com.enonic.wem.api.command.account;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountResult;
import com.enonic.wem.api.command.Command;

public final class FindAccounts
    extends Command<AccountResult>
{
    private AccountQuery query;

    private boolean includeImage;

    private boolean includeMembers;

    public AccountQuery getQuery()
    {
        return this.query;
    }

    public boolean isIncludeImage()
    {
        return this.includeImage;
    }

    public boolean isIncludeMembers()
    {
        return this.includeMembers;
    }

    public FindAccounts query( final AccountQuery query )
    {
        this.query = query;
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
        return Objects.equal(this.query, that.query) &&
            Objects.equal( this.includeImage, that.includeImage ) &&
            Objects.equal( this.includeMembers, that.includeMembers );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.query, this.includeImage, this.includeMembers );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.query, "Account query cannot be null" );
    }
}
