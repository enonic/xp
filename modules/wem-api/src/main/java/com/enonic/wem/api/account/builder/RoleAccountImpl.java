package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.RoleAccount;

final class RoleAccountImpl
    extends NonUserAccountImpl
    implements RoleAccount, RoleAccountBuilder
{
    @Override
    public RoleAccount build()
    {
        return this;
    }

    @Override
    public RoleAccountBuilder members( final AccountKeySet value )
    {
        this.members = value;
        return this;
    }

    @Override
    public RoleAccountBuilder displayName( final String value )
    {
        this.displayName = value;
        return this;
    }
}
