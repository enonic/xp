package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.GroupAccount;

public interface GroupAccountBuilder
    extends NonUserAccountBuilder<GroupAccountBuilder>
{
    public GroupAccount build();
}
