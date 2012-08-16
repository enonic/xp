package com.enonic.wem.api.account.builder;

import com.enonic.wem.api.account.UserAccount;

public interface UserAccountBuilder
    extends AccountBuilder<UserAccountBuilder>
{
    public UserAccountBuilder email( String value );

    public UserAccountBuilder photo( byte[] value );

    public UserAccount build();
}
