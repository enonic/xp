package com.enonic.xp.security.acl;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum IdProviderAccess
{
    READ,
    CREATE_USERS,
    WRITE_USERS, ID_PROVIDER_MANAGER,
    ADMINISTRATOR
}
