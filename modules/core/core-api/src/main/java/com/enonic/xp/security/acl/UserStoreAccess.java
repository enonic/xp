package com.enonic.xp.security.acl;

import com.google.common.annotations.Beta;

@Beta
public enum UserStoreAccess
{
    READ,
    CREATE_USERS,
    WRITE_USERS,
    USER_STORE_MANAGER,
    ADMINISTRATOR
}
