package com.enonic.wem.api.account.result;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKeySet;

public interface AccountResult
    extends Iterable<Account>
{
    public int getSize();

    public int getTotalSize();

    public boolean isEmpty();

    public Account first();

    public Account firstOrNull();

    public AccountKeySet asKeySet();

    public AccountFacets getFacets();
}
