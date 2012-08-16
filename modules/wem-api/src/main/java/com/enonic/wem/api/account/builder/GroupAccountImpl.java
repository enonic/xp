package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;

final class GroupAccountImpl
    extends NonUserAccountImpl
    implements GroupAccount, GroupAccountBuilder
{
    @Override
    public GroupAccount build()
    {
        return this;
    }

    @Override
    public GroupAccountBuilder members( final AccountKeySet value )
    {
        this.members = value;
        return this;
    }

    @Override
    public GroupAccountBuilder displayName( final String value )
    {
        this.displayName = value;
        return this;
    }
}
