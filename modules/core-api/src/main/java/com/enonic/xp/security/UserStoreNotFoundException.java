package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class UserStoreNotFoundException
    extends BaseException
{

    private final UserStoreKey userStoreKey;

    public UserStoreNotFoundException( final UserStoreKey userStoreKey )
    {
        super( "UserStore [{0}] not found", userStoreKey );
        this.userStoreKey = userStoreKey;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }
}
