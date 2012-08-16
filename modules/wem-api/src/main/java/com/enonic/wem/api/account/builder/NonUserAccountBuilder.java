package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.AccountKeySet;

public interface NonUserAccountBuilder<T extends NonUserAccountBuilder>
    extends AccountBuilder<T>
{
    public T members( AccountKeySet value );
}
