package com.enonic.wem.api.account.builder;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;

abstract class AccountImpl
    implements Account
{
    protected AccountKey key;

    protected String displayName;

    @Override
    public final AccountKey getKey()
    {
        return this.key;
    }

    @Override
    public final String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public final DateTime getCreatedTime()
    {
        return null;
    }

    @Override
    public final DateTime getModifiedTime()
    {
        return null;
    }

    @Override
    public final boolean isDeleted()
    {
        return false;
    }

    @Override
    public final boolean isEditable()
    {
        return false;
    }
}
