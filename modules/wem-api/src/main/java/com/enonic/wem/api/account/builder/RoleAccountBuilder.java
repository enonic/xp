package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.RoleAccount;

public interface RoleAccountBuilder
    extends NonUserAccountBuilder<RoleAccountBuilder>
{
    public RoleAccount build();
}
