package com.enonic.wem.api.account.selector;

import com.enonic.wem.api.account.AccountKeySet;

public interface AccountKeySelector
    extends AccountSelector
{
    public AccountKeySet getKeys();
}
