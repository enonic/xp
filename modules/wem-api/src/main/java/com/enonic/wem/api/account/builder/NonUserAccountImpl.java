package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.NonUserAccount;

abstract class NonUserAccountImpl
    extends AccountImpl implements NonUserAccount
{
    protected AccountKeySet members;

    @Override
    public final AccountKeySet getMembers()
    {
        return this.members;
    }
}
